
/**
 * Android Stereo Texture Cube
 * by Andy Modla
 * based on textured cube processing example by Dave Bollinger.
 * 
 * This Android app should be viewed in Google Cardboard to see 
 * the 3D cube and 3D image on a cube face.
 * Accepts keyboard or mouse drag input to rotate the cube.
 * Tested on 1920x1080 display Sony Z1S phone
 *
 * Works with Processing 2.2.1 Android Mode
 * To AndroidManifest.xml I added this line:
 * <uses-feature android:glEsVersion="0x00020000" android:required="true"/>
 */

import android.view.View;
import android.os.Handler;
import android.os.Looper;
import stereo.*;

Stereo stereo = null;
PImage[] photo = null;
PImage[] photoRight = null;
float rotx = PI/4;
float roty = PI/4;
PShape texCube;
PShape texCubeRight;

void setup() {
  size(1920, 1080, OPENGL);
  //size(1920, 1080, P3D);

  // set Landscape orientation
  orientation(LANDSCAPE); 
  // remove navigation bar for full screen
  fullScreen(true);

  // load images used for cube face textures
  photo = new PImage[6];
  photo[0] = loadImage("doll0498_l.jpg");
  photo[1] = loadImage("doll0002.jpg");
  photo[2] = loadImage("doll0001.jpg");
  photo[3] = loadImage("doll0004.jpg");
  photo[4] = loadImage("doll0005.jpg");
  photo[5] = loadImage("doll0006.jpg");
  photoRight = new PImage[6];
  photoRight[0] = loadImage("doll0498_r.jpg");
  photoRight[1] = loadImage("doll0002.jpg");
  photoRight[2] = loadImage("doll0001.jpg");
  photoRight[3] = loadImage("doll0004.jpg");
  photoRight[4] = loadImage("doll0005.jpg");
  photoRight[5] = loadImage("doll0006.jpg");

  fill(255);
  stroke(color(44, 48, 32));

  texCube = createCube(photo);
  texCubeRight = createCube(photoRight);

  /* second constructor, custom eye separation, custom convergence */
  float convPlane =20.0f;
  float eyeSep = (float) (convPlane / 30f); 

  stereo = new Stereo(
  this, eyeSep, 45f, 
  .1f, 
  1000f, Stereo.StereoType.PASSIVE, 
  convPlane);
}

float cx = 0f; 
float cy = 0f; 
float cz = 10f;
boolean start = false;

void draw() {
  background(0, 0, 0, 255);
  stroke(255);
  noStroke();
  PGL pgl = beginPGL();
  if (!start) {
    start = true;
    // only needs to be called repeatedly if you are
    // changing camera position   
    println("Screen Width="+ width + " Height="+height);
    stereo.start(pgl, width, height, 
    cx, cy, cz, 
    0f, 0f, -1f, 
    0f, 1f, 0f);
  }

  // set right eye rendering 
  stereo.setRightEyeView(pgl);    
  drawPhotoCube(texCubeRight);

  // set left eye rendering 
  stereo.setLeftEyeView(pgl);    
  drawPhotoCube(texCube);

  stereo.end(pgl);
}

PShape createCube(PImage[] photo) {
  PShape texCube = createShape(GROUP);
  for (int i=0; i<6; i++) {
    PShape face = createShape();
    face.beginShape(QUADS);
    face.noStroke();
    face.textureMode(NORMAL);
    face.texture(photo[i]);

    if (i == 0) {
      // +Z "front" face
      face.vertex(-1, -1, 1, 0, 0);
      face.vertex( 1, -1, 1, 1, 0);
      face.vertex( 1, 1, 1, 1, 1);
      face.vertex(-1, 1, 1, 0, 1);
    } else if (i==1) {
      // -Z "back" face
      face.vertex( 1, -1, -1, 0, 0);
      face.vertex(-1, -1, -1, 1, 0);
      face.vertex(-1, 1, -1, 1, 1);
      face.vertex( 1, 1, -1, 0, 1);
    } else if (i == 2) {
      // +Y "bottom" face
      face.vertex(-1, 1, 1, 0, 0);
      face.vertex( 1, 1, 1, 1, 0);
      face.vertex( 1, 1, -1, 1, 1);
      face.vertex(-1, 1, -1, 0, 1);
    } else if (i == 3) {
      // -Y "top" face
      face.vertex(-1, -1, -1, 0, 0);
      face.vertex( 1, -1, -1, 1, 0);
      face.vertex( 1, -1, 1, 1, 1);
      face.vertex(-1, -1, 1, 0, 1);
    } else if ( i == 4) {
      // +X "right" face
      face.vertex( 1, -1, 1, 0, 0);
      face.vertex( 1, -1, -1, 1, 0);
      face.vertex( 1, 1, -1, 1, 1);
      face.vertex( 1, 1, 1, 0, 1);
    } else if (i == 5) {
      // -X "left" face
      face.vertex(-1, -1, -1, 0, 0);
      face.vertex(-1, -1, 1, 1, 0);
      face.vertex(-1, 1, 1, 1, 1);
      face.vertex(-1, 1, -1, 0, 1);
    }
    face.endShape(CLOSE);
    texCube.addChild(face);
  }
  
  return texCube;
}

void drawPhotoCube(PShape cube) {
  pushMatrix();
  rotateX(rotx);
  rotateY(roty);
  scale(2.5);
  shape(cube);
  popMatrix();
}

void mouseDragged() {
  float rate = 0.01;
  rotx += (pmouseY-mouseY) * rate;
  roty += (mouseX-pmouseX) * rate;
}

void mouseReleased() {
  fullScreen(true);
}

int KEYCODE_MEDIA_NEXT = 87; // RIGHT
int KEYCODE_MEDIA_PREVIOUS = 88;  // LEFT
int KEYCODE_MEDIA_FAST_FORWARD = 90;  // UP
int KEYCODE_MEDIA_REWIND = 89;  // DOWN
int KEYCODE_ENTER = 66;
public void keyPressed()
{
  float rate = 0.01;
  println("keyPressed="+keyCode);
  if (keyCode == RIGHT || keyCode == KEYCODE_MEDIA_NEXT) {
    roty += rate;
  }
  else if (keyCode == DOWN || keyCode == KEYCODE_MEDIA_REWIND) {
    rotx += -rate;
  }
  else if (keyCode == LEFT || keyCode == KEYCODE_MEDIA_PREVIOUS) {
    roty += -rate;
  }
  else if (keyCode == UP || keyCode == KEYCODE_MEDIA_FAST_FORWARD) {
    rotx += rate;
  }
  else if (keyCode == KEYCODE_ENTER) {
    fullScreen(true);
  }
}

/**
 * Make full screen by removing the navigation bar
 */
void fullScreen(final boolean nav) {
  Handler refresh;
  refresh = new Handler(Looper.getMainLooper());
  // have to call from main thread
  refresh.post(new Runnable() {
    public void run()
    {
      setNavVisibility(!nav);
    }
  }
  );
}

// define system constants because processing-android build level is out of date
int SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN = 1024;
int SYSTEM_UI_FLAG_HIDE_NAVIGATION = 2;
int SYSTEM_UI_FLAG_LAYOUT_STABLE = 256;
int SYSTEM_UI_FLAG_FULLSCREEN = 4;
int SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION = 512;
private void setNavVisibility(boolean visible) {
  int newVis = SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
  if (!visible) {
    newVis |= View.SYSTEM_UI_FLAG_LOW_PROFILE | SYSTEM_UI_FLAG_FULLSCREEN
      | SYSTEM_UI_FLAG_HIDE_NAVIGATION;
  }
  // Set the new desired visibility.
  final View decorView = this.getWindow().getDecorView();
  decorView.setSystemUiVisibility(newVis);
}

