
package com.pacoworks.reactiveinputs;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.Builder;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Paco on 09/11/2014. See LICENSE.md
 */
public class ReactiveInputs {
    private final SerializedSubject<Integer, Integer> moves;

    @Getter
    @Accessors(prefix = "m")
    private final int mFramesPerSecond;

    private int mWindowDurationMs;

    @Builder
    public ReactiveInputs(int framesPerSecond) {
        if (framesPerSecond < 1) {
            throw new IllegalArgumentException("Frames Per Second must be more than 0");
        }
        moves = new SerializedSubject<>(PublishSubject.<Integer> create());
        mFramesPerSecond = framesPerSecond;
        mWindowDurationMs = 1000 / mFramesPerSecond;
    }

    public Observable<List<?>> observeMove(final IKnownMove move) {
        return moves
                .throttleFirst(mWindowDurationMs, TimeUnit.MILLISECONDS)
                .buffer(mWindowDurationMs
                                * (move.getLeniencyFrames() + move.getInputSequence().size()),
                        mWindowDurationMs, TimeUnit.MILLISECONDS)
                .map(results -> {
                    if (results.size() < move.getInputSequence().size()) {
                        return new ArrayList<>();
                    }
                    List<Integer> inputs = new ArrayList<>();
                    int startPosition = results.size() - move.getInputSequence().size();
                    startPosition = (startPosition - move.getMaxInputErrors() < 0) ? 0
                            : startPosition - move.getMaxInputErrors();
                    for (int i = startPosition; i < results.size(); i++) {
                        inputs.add(results.get(i));
                    }
                    return inputs;
                }).filter(windowMoves -> {
                    List<Integer> inputSequence = move.getInputSequence();
                    int maxErrors = move.getMaxInputErrors();
                    int moveIndex = 0;
                    for (int i = 0; i < windowMoves.size(); i++) {
                        boolean equal = windowMoves.get(i) == inputSequence.get(moveIndex);
                        if (equal && moveIndex + 1 == inputSequence.size()) {
                            return true;
                        } else if (equal) {
                            moveIndex++;
                        } else if (maxErrors == 0 || i + maxErrors < inputSequence.size()) {
                            return false;
                        } else {
                            maxErrors--;
                        }
                    }
                    return false;
                });
    }

    public void sendInputKeycode(int keyCode) {
        moves.onNext(keyCode);
    }
}
