package uk.ac.reading.sis05kol.mooc;

//Other parts of the android libraries that we use
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class TheGame extends GameThread{

	//Will store the image of a ball
	private Bitmap mBall;
	
	//The X and Y position of the ball on the screen (middle of ball)
	private float mBallX = 0;
	private float mBallY = 0;
	
	//The speed (pixel/second) of the ball in direction X and Y
	private float mBallSpeedX = 0;
	private float mBallSpeedY = -50;
	
	//Setup variables for paddle image and it's X movement
	private Bitmap mPaddle;
	private float mPaddleX = 0;
	private float mPaddleSpeedX = 0;
	
	private float mMinDistanceBetweenRedBallAndBigBall = 0;
	
	//Setup Smiley Ball
	private Bitmap mSmileyBall;
	private float mSmileyBallX = -100;
	private float mSmileyBallY = -100;
	
	//Setup Sad Balls
	private Bitmap mSadBall;
	private float[] mSadBallX = {-100,-100,-100};
	private float[] mSadBallY = new float[3];
	

	//This is run before anything else, so we can prepare things here
	public TheGame(GameView gameView) {
		//House keeping
		super(gameView);
		
		//Prepare the image so we can draw it on the screen (using a canvas)
		mBall = BitmapFactory.decodeResource
				(gameView.getContext().getResources(), 
				R.drawable.james_ball);
		
		//Prepare the paddle image
		mPaddle = BitmapFactory.decodeResource
				(gameView.getContext().getResources(), 
				R.drawable.yellow_ball);
		
		//Prepare Smiley Ball
		mSmileyBall = BitmapFactory.decodeResource
				(gameView.getContext().getResources(), 
				R.drawable.smiley_ball);
		//Prepare Sad Ball
		mSadBall = BitmapFactory.decodeResource
				(gameView.getContext().getResources(), 
				R.drawable.sad_ball);



	}
	
	//This is run before a new game (also after an old game)
	@Override
	public void setupBeginning() {
		//Initialise speeds
		mBallSpeedX = -200; 
		mBallSpeedY = -200;
		
		//Place the ball in the middle of the screen.
		//mBall.Width() and mBall.getHeigh() gives us the height and width of the image of the ball
		mBallX = mCanvasWidth / 2;
		mBallY = mCanvasHeight / 2;
		
		//Place the paddle in the center bottom of the screen
		mPaddleX = mCanvasWidth / 2;
		
		//Place Smiley Ball on screen
		mSmileyBallX = mCanvasWidth/2;
		mSmileyBallY = mSmileyBall.getHeight()/2;
		
		//Place Sad Balls
		mSadBallX[0] = mCanvasWidth/3;
		mSadBallY[0] = mCanvasHeight/3;
		mSadBallX[1] = mCanvasWidth - mCanvasWidth/3;
		mSadBallY[1] = mCanvasHeight/3;
		mSadBallX[2] = mCanvasWidth/2;
		mSadBallY[2] = mCanvasHeight/5;
		
		
		//Calculate distance for collision (squared to avoid sqrt later)
		mMinDistanceBetweenRedBallAndBigBall = (mBall.getWidth()/2 + mPaddle.getWidth()/2) * (mBall.getWidth()/2 + mPaddle.getWidth()/2);
	}

	@Override
	protected void doDraw(Canvas canvas) {
		//If there isn't a canvas to draw on do nothing
		//It is ok not understanding what is happening here
		if(canvas == null) return;
		
		super.doDraw(canvas);
		
		//draw the image of the ball using the X and Y of the ball
		//drawBitmap uses top left corner as reference, we use middle of picture
		//null means that we will use the image without any extra features (called Paint)
		canvas.drawBitmap(mBall, mBallX - mBall.getWidth() / 2, mBallY - mBall.getHeight() / 2, null);

		//draw the image of the paddle on the screen
		canvas.drawBitmap(mPaddle, mPaddleX - mPaddle.getWidth()/2, mCanvasHeight - mPaddle.getHeight()/2, null);

		//draw the Smiley Ball on the screen
		canvas.drawBitmap(mSmileyBall, mSmileyBallX - mSmileyBall.getWidth()/2, mSmileyBallY - mSmileyBall.getHeight()/2, null);

		//draw the Sad Balls
		for (int i = 0; i < mSadBallX.length; i++) {
			canvas.drawBitmap(mSadBall, mSadBallX[i] - mSadBall.getWidth()/2, mSadBallY[i] - mSadBall.getHeight()/2, null);
		}
	}
	
	//This is run whenever the phone is touched by the user
	
	@Override
	protected void actionOnTouch(float x, float y) {
		//Move the paddle
		mPaddleX = x - mPaddle.getWidth()/2;

	}
	
	
	
	//This is run whenever the phone moves around its axises 
	
	@Override
	protected void actionWhenPhoneMoved(float xDirection, float yDirection, float zDirection) {
		//Move the paddle if it's on the screen
		if (mPaddleX>=0 && mPaddleX<=mCanvasWidth) {
		  mPaddleX = mPaddleX - xDirection;
		  
		  if (mPaddleX<0) mPaddleX=0;
		  if (mPaddleX>mCanvasWidth) mPaddleX=mCanvasWidth;
		}
	}
	
	
	//This is run just before the game "scenario" is printed on the screen
	@Override
	protected void updateGame(float secondsElapsed) {
		float distanceBetweenBallAndPaddle;
		
		if (mBallSpeedY > 0) {
			distanceBetweenBallAndPaddle = (mPaddleX - mBallX) * (mPaddleX - mBallX) + (mCanvasHeight - mBallY) * (mCanvasHeight - mBallY);
			if (distanceBetweenBallAndPaddle <= mMinDistanceBetweenRedBallAndBigBall) {
				float velocityOfBall = (float) Math.sqrt(mBallSpeedX*mBallSpeedX + mBallSpeedY*mBallSpeedY);

				mBallSpeedX = mBallX-mPaddleX;
				mBallSpeedY = mBallY-mCanvasHeight;
				
				float newVelocity = (float) Math.sqrt(mBallSpeedX*mBallSpeedX + mBallSpeedY*mBallSpeedY);
				
				mBallSpeedX = mBallSpeedX * velocityOfBall/newVelocity;
				mBallSpeedY = mBallSpeedY * velocityOfBall/newVelocity;
				
			}
		}
		//Check whether the ball is touching the edge of the screen and
		//whether it is moving off or not
		if ((mBallX <= mBall.getWidth()/2 && mBallSpeedX<0)|| (mBallX >= mCanvasWidth-mBall.getWidth()/2 && mBallSpeedX>0)) {
			mBallSpeedX = -mBallSpeedX;
		}
		
		
		//Check if ball hits Smiley Ball
		distanceBetweenBallAndPaddle = (mSmileyBallX - mBallX) * (mSmileyBallX - mBallX) + (mSmileyBallY - mBallY) *(mSmileyBallY - mBallY);
		
		//Check if the actual distance is lower than the allowed => collision
		if(mMinDistanceBetweenRedBallAndBigBall >= distanceBetweenBallAndPaddle) {

			//Get the present velocity (this should also be the velocity going away after the collision)
			float velocityOfBall = (float) Math.sqrt(mBallSpeedX*mBallSpeedX + mBallSpeedY*mBallSpeedY);

			//Change the direction of the ball
			mBallSpeedX = mBallX - mSmileyBallX;
			mBallSpeedY = mBallY - mSmileyBallY;

			//Get the velocity after the collision
			float newVelocity = (float) Math.sqrt(mBallSpeedX*mBallSpeedX + mBallSpeedY*mBallSpeedY);

			//using the fraction between the original velocity and present velocity to calculate the needed
			//speeds in X and Y to get the original velocity but with the new angle.
			mBallSpeedX = mBallSpeedX * velocityOfBall / newVelocity;
			mBallSpeedY = mBallSpeedY * velocityOfBall / newVelocity;
			
			//Increase score
			updateScore(1);
		}
		
		//Check for hit on Sad Ball
		//Loop through all SadBalls
		for(int i = 0; i < mSadBallX.length; i++) {
			//Perform collisions (if necessary) between SadBall in position i and the red ball
			
			//Get actual distance (without square root - remember?) between the mBall and the ball being checked
			distanceBetweenBallAndPaddle = (mSadBallX[i] - mBallX) * (mSadBallX[i] - mBallX) + (mSadBallY[i] - mBallY) *(mSadBallY[i] - mBallY);
		
			//Check if the actual distance is lower than the allowed => collision
			if(mMinDistanceBetweenRedBallAndBigBall >= distanceBetweenBallAndPaddle) {

				//Get the present velocity (this should also be the velocity going away after the collision)
				float velocityOfBall = (float) Math.sqrt(mBallSpeedX*mBallSpeedX + mBallSpeedY*mBallSpeedY);

				//Change the direction of the ball
				mBallSpeedX = mBallX - mSadBallX[i];
				mBallSpeedY = mBallY - mSadBallY[i];

				//Get the velocity after the collision
				float newVelocity = (float) Math.sqrt(mBallSpeedX*mBallSpeedX + mBallSpeedY*mBallSpeedY);

				//using the fraction between the original velocity and present velocity to calculate the needed
				//speeds in X and Y to get the original velocity but with the new angle.
				mBallSpeedX = mBallSpeedX * velocityOfBall / newVelocity;
				mBallSpeedY = mBallSpeedY * velocityOfBall / newVelocity;
			}
		}

		
		//Check whether the ball is touching the top of the screen and
		//whether it is moving off or not
		if ((mBallY <= mBall.getHeight()/2 && mBallSpeedY<0)) {
			mBallSpeedY = -mBallSpeedY;
		}
		
		if (mBallY >= mCanvasHeight-mBall.getHeight()/2 && mBallSpeedY>0) {
			setState(GameThread.STATE_LOSE);
		}

		//Move the ball's X and Y using the speed (pixel/sec)
		mBallX = mBallX + secondsElapsed * mBallSpeedX;
		mBallY = mBallY + secondsElapsed * mBallSpeedY;

	}
}

// This file is part of the course "Begin Programming: Build your first mobile game" from futurelearn.com
// Copyright: University of Reading and Karsten Lundqvist
// It is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// It is is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// 
// You should have received a copy of the GNU General Public License
// along with it.  If not, see <http://www.gnu.org/licenses/>.
