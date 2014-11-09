
package com.pacoworks.reactiveinputs;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import rx.schedulers.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.pacoworks.reactiveinputs.MyGdxGame.KEY_WRAPPER.KEY_DOWN;
import static com.pacoworks.reactiveinputs.MyGdxGame.KEY_WRAPPER.KEY_ONE;
import static com.pacoworks.reactiveinputs.MyGdxGame.KEY_WRAPPER.KEY_RIGHT;

@Slf4j
public class MyGdxGame extends ApplicationAdapter {
    SpriteBatch batch;

    Texture img;

    private Random random;

    private ReactiveInputs inputs;

    @Override
    public void create() {
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
        random = new Random();
        inputs = ReactiveInputs.builder().stepsPerSecond(60).build();
        Hadouken hadouken = new Hadouken();
        inputs.observeMove(hadouken)
                .subscribeOn(Schedulers.computation())
                .subscribe(
                        integers -> log.debug("{} detected! - {}", hadouken.getMoveName(), integers));
        // inputs.observeMove(new Shoryuken());
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(img, 0, 0);
        batch.end();
        inputs.sendInputKeycode(random.nextInt(KEY_WRAPPER.values().length));
    }

    @ToString
    public static class Hadouken implements IKnownMove {
        @Getter
        private final List<Integer> inputSequence = Arrays.asList(KEY_DOWN.ordinal(),
                KEY_RIGHT.ordinal(), KEY_ONE.ordinal());

        @Getter
        private final String moveName = "Hadouken";

        @Getter
        @Accessors(prefix = "m")
        private final int mLeniencyFrames = 4;

        @Getter
        @Accessors(prefix = "m")
        private final int mMaxInputErrors = 2;
    }

    @ToString
    public static class Shoryuken implements IKnownMove {
        @Getter
        private final List<Integer> inputSequence = Arrays.asList(KEY_RIGHT.ordinal(),
                KEY_DOWN.ordinal(), KEY_RIGHT.ordinal(), KEY_ONE.ordinal());

        @Getter
        private final String moveName = "Shoryuken";

        @Getter
        @Accessors(prefix = "m")
        private final int mLeniencyFrames = 4;

        @Getter
        @Accessors(prefix = "m")
        private final int mMaxInputErrors = 2;
    }

    /**
    * Created by Paco on 09/11/2014.
    * See LICENSE.md
    */
    public static enum KEY_WRAPPER {
        UNKNOWN("?"), KEY_UP("Up"), KEY_DOWN("Down"), KEY_LEFT("Left"), KEY_RIGHT("Right"), KEY_ONE("One");
        @Getter
        private final String mKeyName;

        private KEY_WRAPPER(@NonNull String keyName) {
            mKeyName = keyName;
        }

        private static KEY_WRAPPER fromKeycode(int keyCode){
            KEY_WRAPPER[] values = KEY_WRAPPER.values();
            if (keyCode < 1 || keyCode >= values.length){
                return UNKNOWN;
            }
            return values[keyCode];
        }
    }
}
