
package com.pacoworks.reactiveinputs;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.Builder;
import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

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

    private int mFrameDurationMs;

    @Builder
    public ReactiveInputs(int framesPerSecond) {
        if (framesPerSecond < 1) {
            throw new IllegalArgumentException("Frames Per Second must be more than 0");
        }
        moves = new SerializedSubject<>(PublishSubject.<Integer> create());
        mFramesPerSecond = framesPerSecond;
        mFrameDurationMs = 1000 / mFramesPerSecond;
    }

    public Observable<List<Integer>> observeMove(final IKnownMove move) {
        return moves
                .throttleFirst(mFrameDurationMs, TimeUnit.MILLISECONDS)
                .buffer(mFrameDurationMs
                        * (move.getLeniencyFrames() + move.getInputSequence().size()),
                        mFrameDurationMs, TimeUnit.MILLISECONDS)
                .filter(new Func1<List<Integer>, Boolean>() {
                    @Override
                    public Boolean call(List<Integer> results) {
                        return results.size() >= move.getInputSequence().size();
                    }
                })
                .filter(new Func1<List<Integer>, Boolean>() {
                            @Override
                            public Boolean call(List<Integer> windowMoves) {
                                List<Integer> inputSequence = move.getInputSequence();
                                int maxErrors = move.getMaxInputErrors();
                                int moveIndex = 0;
                                for (int i = 0; i < windowMoves.size(); i++) {
                                    boolean equal = windowMoves.get(i).equals(inputSequence.get(moveIndex));
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
                            }
                        });
    }

    public void sendInputKeycode(int keyCode) {
        moves.onNext(keyCode);
    }
}
