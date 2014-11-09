package com.pacoworks.reactiveinputs;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

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
		inputs = new ReactiveInputs();
		inputs.subscribeMove(new ReactiveInputs.Hadouken());
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
}
