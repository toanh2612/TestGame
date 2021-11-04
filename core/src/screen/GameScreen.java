package screen;


import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import assets.Assets;
import utility.Constant;


public class GameScreen implements Screen {
    //screen
    private Camera camera;
    private Viewport viewport;
    //graphics
    private SpriteBatch batch;
    private Texture background;

    //timing
    private int backgroundOffset;


    public GameScreen(){
        camera = new OrthographicCamera(); //overview
        viewport = new StretchViewport(Constant.WIDTH,Constant.HEIGHT,camera);
        background = new Texture(Assets.BACKGROUND);
        backgroundOffset = 0;

        batch = new SpriteBatch();


    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        batch.begin();

        //scrolling background
        backgroundOffset++;
        if(backgroundOffset % Constant.HEIGHT == 0) {
            backgroundOffset = 0;
        }
        //loop background
        batch.draw(background,0,-backgroundOffset, Constant.WIDTH,Constant.HEIGHT);
        batch.draw(background,0,-backgroundOffset + Constant.HEIGHT, Constant.WIDTH,Constant.HEIGHT);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height,true);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
