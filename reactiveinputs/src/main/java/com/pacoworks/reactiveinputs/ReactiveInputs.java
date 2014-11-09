
package com.pacoworks.reactiveinputs;

import static com.pacoworks.reactiveinputs.KEY_WRAPPER.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by Paco on 09/11/2014. See LICENSE.md
 */
@Slf4j
public class ReactiveInputs {
    private final PublishSubject<SingleInput> moves;

    @Getter
    @Setter
    @Accessors(prefix = "m")
    private int mFramesPerSecond;

    @Getter
    @Setter
    @Accessors(prefix = "m")
    private int mLeniencyFrames;

    public ReactiveInputs() {
        moves = PublishSubject.<SingleInput> create();
        mFramesPerSecond = 60;
        mLeniencyFrames = 4;
        int windowDurationMs = 1000 / mFramesPerSecond;
        moves.doOnNext(move -> log.error("{}", move))
                .throttleFirst(windowDurationMs, TimeUnit.MILLISECONDS)
                .map(input -> input.mKeyCode);
    }

    public void subscribeMove(final IKnownMove move) {
        int windowDurationMs = 1000 / mFramesPerSecond;
        Observable moveInput = Observable.just(move.getInputSequence());
        moves.buffer(windowDurationMs * mLeniencyFrames * move.getInputSequence().length,
                TimeUnit.MILLISECONDS)
        // .takeLast(move.getInputSequence().length)
                .forEach(message -> log.error("{} - {}", move.getMoveName(), message));
    }

    public void sendMove(SingleInput input) {
        moves.onNext(input);
    }

    @Data
    @Accessors(prefix = "m")
    public static class SingleInput {
        private final int mKeyCode;

        public SingleInput(int keyCode) {
            this.mKeyCode = keyCode;
        }

        public SingleInput(@NonNull KEY_WRAPPER keyCodeWrap) {
            this.mKeyCode = keyCodeWrap.ordinal();
        }
    }

    @ToString
    public static class Hadouken implements IKnownMove {
        @Getter
        private int[] inputSequence = new int[] {
                KEY_DOWN.ordinal(), KEY_RIGHT.ordinal(), KEY_ONE.ordinal()
        };

        @Getter
        private String moveName = "Hadouken";
    }

    @ToString
    public static class Shoryuken implements IKnownMove {
        @Getter
        private int[] inputSequence = new int[] {
                KEY_DOWN.ordinal(), KEY_RIGHT.ordinal(), KEY_ONE.ordinal()
        };

        @Getter
        private String moveName = "Shoryuken";
    }
}
