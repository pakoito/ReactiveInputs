package com.pacoworks.reactiveinputs;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.pacoworks.reactiveinputs.KEY_WRAPPER.KEY_DOWN;
import static com.pacoworks.reactiveinputs.KEY_WRAPPER.KEY_ONE;
import static com.pacoworks.reactiveinputs.KEY_WRAPPER.KEY_RIGHT;

@Slf4j
public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	private Random random;
	private ReactiveInputs inputs;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		random = new Random();
		inputs = ReactiveInputs.builder().stepsPerSecond(60).build();
		inputs.subscribeMove(new Hadouken());
//		inputs.subscribeMove(new Shoryuken());
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
		inputs.sendMove(random.nextInt(KEY_WRAPPER.values().length));
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
}
