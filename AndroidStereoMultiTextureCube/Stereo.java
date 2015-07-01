package stereo;

import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.*;

/**
 * Stereo derived from repository
 *
 * https://github.com/CreativeCodingLab/stereo
 *
 * https://github.com/CreativeCodingLab/stereo/blob/master/src/stereo/Stereo.java
 *
 * Modified by Andy Modla to only use Processing OPENGL API.
 * left() and right() not tested.
 
 * @class Stereo
 * @description
 */
public class Stereo {

    public enum StereoType {
        ACTIVE,
        PASSIVE,
        ANAGLYPH_REDLEFT_BLUERIGHT,
        ANAGLYPH_REDLEFT_CYANRIGHT,
        ANAGLYPH_CYANLEFT_REDRIGHT,
        ANAGLYPH_BLUELEFT_REDRIGHT
    }

    public StereoType stereoType = null;
    public float eyeSeperation;
    PApplet app = null;
    float aspectRatio, nearPlane, farPlane, widthdiv2, convPlane;
    int width, height;
    float fovy = 45;
    float posx, posy, posz;
    float dirx, diry, dirz;
    float upx, upy, upz;
    float rightx, righty, rightz;
    PGL pgl;

    // constructor convergence distance is calculated as 30 times the eye separation
    public Stereo(PApplet app, float eyeSeperation, float fovy, float nearPlane, float farPlane, Stereo.StereoType stereoType) {
        this.app = app;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
        this.fovy = fovy;
        this.eyeSeperation = eyeSeperation;
        this.convPlane = eyeSeperation * 30.0f;
        this.stereoType = stereoType;

    }

    // second constructor eye separation and conv plane are explictly set
    public Stereo(PApplet app, float eyeSeperation, float fovy, float nearPlane, float farPlane, StereoType stereoType, float convPlane) {
        this.app = app;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
        this.fovy = fovy;
        this.eyeSeperation = eyeSeperation;
        this.convPlane = convPlane;
        this.stereoType = stereoType;
    }

    // third constructor eye separation is calculated as 1/30th from the conv distance
    public Stereo(PApplet app, float fovy, float nearPlane, float farPlane, Stereo.StereoType stereoType, float convPlane) {
        this.app = app;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
        this.fovy = fovy;
        this.convPlane = convPlane;
        this.eyeSeperation = convPlane / 30.0f;
        this.stereoType = stereoType;
    }

    // fourth constructor convergence distance is calculated from the  near and far planes, eye separation is calculated as 1/30th from the conv distance
    public Stereo(PApplet app, float fovy, float nearPlane, float farPlane, Stereo.StereoType stereoType) {
        this.app = app;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
        this.fovy = fovy;
        this.convPlane = (float) (nearPlane + (farPlane - nearPlane) / 100.0f);
        this.eyeSeperation = (float) convPlane / 30.f;
        this.stereoType = stereoType;
    }

    public void end(PGL pgl) {
        pgl.colorMask(true, true, true, true);
    }

    public void start(PGL pgl, int width, int height,
                      float posx, float posy, float posz,
                      float dirx, float diry, float dirz,
                      float upx, float upy, float upz) {
        this.width = width;  
        this.height = height;
        if (this.stereoType == StereoType.PASSIVE) {
            this.aspectRatio = (float) (width / 2.0f) / (float) height;
        } else {
            this.aspectRatio = (float) width / (float) height;
        }

        this.widthdiv2 = nearPlane * (float) Math.tan(this.fovy / 2); // aperture in radians
        this.posx = posx;
        this.posy = posy;
        this.posz = posz;

        this.dirx = dirx;
        this.diry = diry;
        this.dirz = dirz;

        this.upx = upx;
        this.upy = upy;
        this.upz = upz;

        PVector cdir = new PVector(this.dirx, this.diry, this.dirz);
        PVector cup = new PVector(this.upx, this.upy, this.upz);
        PVector cright = cdir.cross(cup);

        this.rightx = cright.x * eyeSeperation / 2.0f;
        this.righty = cright.y * eyeSeperation / 2.0f;
        this.rightz = cright.z * eyeSeperation / 2.0f;
    }

    public void left(PGL pgl) {

        switch (this.stereoType) {
//            case ACTIVE:
//                GLES20.glDrawBuffer(GL_BACK_LEFT);
//                GLES20.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//                break;
//            case ANAGLYPH_REDLEFT_CYANRIGHT:
//                GLES20.glColorMask(true, false, false, true);
//                GLES20.glViewport(0, 0, this.w, this.h);
//                break;
//            case ANAGLYPH_CYANLEFT_REDRIGHT:
//                GLES20.glColorMask(false, true, true, true);
//                GLES20.glViewport(0, 0, this.w, this.h);
//                break;
//            case ANAGLYPH_REDLEFT_BLUERIGHT:
//                GLES20.glColorMask(true, false, false, true);
//                GLES20.glViewport(0, 0, this.w, this.h);
//                break;
//            case ANAGLYPH_BLUELEFT_REDRIGHT:
//                GLES20.glColorMask(false, false, true, true);
//                GLES20.glViewport(0, 0, this.w, this.h);
//                break;
            case PASSIVE:
                pgl.viewport(0, 0, this.width / 2, this.height);
                break;
            default:
                pgl.viewport(0, 0, this.width, this.height);
        }

        if (this.stereoType == StereoType.PASSIVE) {
            pgl.viewport(0, 0, this.width / 2, this.height);
        } else {
            pgl.viewport(0, 0, this.width, this.height);
        }
        float top = widthdiv2;
        float bottom = -widthdiv2;
        float left = -aspectRatio * widthdiv2 + 0.5f * eyeSeperation * nearPlane / convPlane;
        float right = aspectRatio * widthdiv2 + 0.5f * eyeSeperation * nearPlane / convPlane;
        app.frustum(left, right, bottom, top, nearPlane, farPlane);

        app.camera(
                posx - rightx, posy - righty, posz - rightz,
                posx - rightx + dirx, posy - righty + diry, posz - rightz + dirz,
                upx, upy, upz);
    }

    /**
     * Set the modelview matrix
     *
     * @param pgl
     */
    public void right(PGL pgl) {

        switch (this.stereoType) {
//            case ACTIVE:
//                GLES20.glDrawBuffer(GL_BACK_RIGHT);
//                GLES20.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//                break;
//            case ANAGLYPH_REDLEFT_CYANRIGHT:
//                GLES20.glColorMask(false, true, true, true);
//                GLES20.glViewport(0, 0, this.w, this.h);
//                break;
//            case ANAGLYPH_CYANLEFT_REDRIGHT:
//                GLES20.glColorMask(true, false, false, true);
//                GLES20.glViewport(0, 0, this.w, this.h);
//                break;
//            case ANAGLYPH_REDLEFT_BLUERIGHT:
//                GLES20.glColorMask(false, false, true, true);
//                GLES20.glViewport(0, 0, this.w, this.h);
//                break;
//            case ANAGLYPH_BLUELEFT_REDRIGHT:
//                GLES20.glColorMask(true, false, false, true);
//                GLES20.glViewport(0, 0, this.w, this.h);
//                break;
            case PASSIVE:
                pgl.viewport(this.width / 2, 0, this.width / 2, this.height);
                break;
            default:
                pgl.viewport(0, 0, this.width, this.height);
        }

        if (this.stereoType == StereoType.PASSIVE) {
            pgl.viewport(this.width / 2, 0, this.width / 2, this.height);
        } else {
            pgl.viewport(0, 0, this.width, this.height);
        }

        float top = widthdiv2;
        float bottom = -widthdiv2;
        float left = -aspectRatio * widthdiv2 - 0.5f * eyeSeperation * nearPlane / convPlane;
        float right = aspectRatio * widthdiv2 - 0.5f * eyeSeperation * nearPlane / convPlane;
        app.frustum(left, right, bottom, top, nearPlane, farPlane);

        app.camera(
                posx + rightx, posy + righty, posz + rightz,
                posx + rightx + dirx, posy + righty + diry, posz + rightz + dirz,
                upx, upy, upz);
    }

    /**
     * Set OpenGL context for rendering the right eye view
     * @param pgl Processing-OpenGL abstraction layer.
     */
    public void setRightEyeView(PGL pgl) {
        // Adjusts viewport based on stereo type
        if(this.stereoType == StereoType.PASSIVE) {
            pgl.viewport(this.width / 2, 0, this.width / 2, this.height);
        } else {
            pgl.viewport(0, 0, this.width, this.height);
        }

        // Set frustum
        float top = (float)(widthdiv2);
        float bottom = (float)(-widthdiv2);
        float left = (float)(-aspectRatio * widthdiv2 - 0.5f * eyeSeperation * nearPlane / convPlane);
        float right = (float)(aspectRatio * widthdiv2 - 0.5f * eyeSeperation * nearPlane / convPlane);
        app.frustum(left, right, bottom, top, (float) nearPlane, (float) farPlane);

        // Set camera
        app.camera(
                posx + rightx, posy + righty, posz + rightz,
                posx + rightx + dirx, posy + righty + diry, posz + rightz + dirz,
                upx, upy, upz
        );
    }

    /**
     * Set OpenGL context for rendering the left eye view
     * @param pgl Processing-OpenGL abstraction layer.
     */
    public void setLeftEyeView(PGL pgl) {
        // Adjusts viewport based on stereo type
        if(this.stereoType == StereoType.PASSIVE) {
            pgl.viewport(0, 0, this.width / 2, this.height);
        } else {
            pgl.viewport(0, 0, this.width, this.height);
        }

        // Set frustum
        float top = (float)(widthdiv2);
        float bottom = (float)(-widthdiv2);
        float left = (float)(-aspectRatio * widthdiv2 + 0.5f * eyeSeperation * nearPlane / convPlane);
        float right = (float)(aspectRatio * widthdiv2 + 0.5f * eyeSeperation * nearPlane / convPlane);
        app.frustum(left, right, bottom, top, (float) nearPlane, (float) farPlane);

        // Set camera
        app.camera(
                posx - rightx, posy - righty, posz - rightz,
                posx - rightx + dirx, posy - righty + diry, posz - rightz + dirz,
                upx, upy, upz
        );
    }
}


