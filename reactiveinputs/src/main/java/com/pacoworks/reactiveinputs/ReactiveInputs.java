
package com.pacoworks.reactiveinputs;

import static com.pacoworks.reactiveinputs.KEY_WRAPPER.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.Builder;
import lombok.extern.slf4j.Slf4j;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by Paco on 09/11/2014. See LICENSE.md
 */
@Slf4j
public class ReactiveInputs {
    private final PublishSubject<Integer> moves;

    @Getter
    @Accessors(prefix = "m")
    private int mFramesPerSecond;

    @Builder
    public ReactiveInputs(int stepsPerSecond) {
        if (stepsPerSecond < 1){
            throw new IllegalArgumentException("Frames Per Second must be more than 0");
        }
        moves = PublishSubject.<Integer> create();
        mFramesPerSecond = stepsPerSecond;
    }

    public void subscribeMove(final IKnownMove move) {
        int windowDurationMs = 1000 / mFramesPerSecond;
        moves.throttleFirst(windowDurationMs, TimeUnit.MILLISECONDS)
                .doOnNext(input -> log.error("Input {}", input))
                .buffer(windowDurationMs * (move.getLeniencyFrames() + move.getInputSequence().size()),
                        windowDurationMs, TimeUnit.MILLISECONDS)
                .map(results -> {
                    if (results.size() < move.getInputSequence().size()) {
                        return new ArrayList<>();
                    }
                    ArrayList<Integer> inputs = new ArrayList<>();
                    for (int i = results.size() - move.getInputSequence().size(); i < results
                            .size(); i++) {
                        inputs.add(results.get(i));
                    }
                    return inputs;
                }).filter(windowMoves -> {
                    List<Integer> inputSequence = move.getInputSequence();
                    if (windowMoves.size() != inputSequence.size()) {
                        return false;
                    }
                    for (int i = 0; i < windowMoves.size(); i++) {
                        if (windowMoves.get(i) != inputSequence.get(i)) {
                            return false;
                        }
                    }
                    return true;
                }).subscribeOn(Schedulers.newThread())
                .subscribe(message -> log.debug("{} detected! - {}", move.getMoveName(), message));
    }

    public void sendMove(int input) {
        moves.onNext(input);
    }
}
