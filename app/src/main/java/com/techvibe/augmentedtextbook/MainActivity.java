 package com.techvibe.augmentedtextbook;

 import android.graphics.Bitmap;
 import android.graphics.BitmapFactory;
 import android.net.Uri;
 import android.os.Bundle;
 import android.util.Log;

 import androidx.appcompat.app.AppCompatActivity;

 import com.google.ar.core.Anchor;
 import com.google.ar.core.AugmentedImage;
 import com.google.ar.core.AugmentedImageDatabase;
 import com.google.ar.core.Config;
 import com.google.ar.core.Frame;
 import com.google.ar.core.Session;
 import com.google.ar.core.TrackingState;
 import com.google.ar.sceneform.AnchorNode;
 import com.google.ar.sceneform.FrameTime;
 import com.google.ar.sceneform.Scene;
 import com.google.ar.sceneform.rendering.ModelRenderable;
 import com.google.ar.sceneform.ux.ArFragment;

 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Map;

 public class MainActivity extends AppCompatActivity implements Scene.OnUpdateListener{

    private CustomARFragment arFragment;
     HashMap<AugmentedImage,Anchor> augmentedImageMap=new HashMap<AugmentedImage,Anchor>();
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arFragment= (CustomARFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getPlaneDiscoveryController().setInstructionView(null);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this);
    }

    public void setupDatabase(Config config, Session session)
    {
        Bitmap earthBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.earth);
        Bitmap ironmanBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ironman);
        Bitmap penguinBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.penguin);
        AugmentedImageDatabase aid = new AugmentedImageDatabase(session);
        aid.addImage("earth",earthBitmap);
        aid.addImage("ironman",ironmanBitmap);
        aid.addImage("penguin",penguinBitmap);
        config.setAugmentedImageDatabase(aid);

    }

     @Override
     public void onUpdate(FrameTime frameTime) {

         Frame frame = arFragment.getArSceneView().getArFrame();
         if(frame == null)
         {
             return;
         }
         Collection<AugmentedImage> images = frame.getUpdatedTrackables(AugmentedImage.class);
         for (AugmentedImage image : images) {

             switch(image.getTrackingState()) {
                 case TRACKING:
                     Log.d("**Tracking:","Inside Tracking....."+image.getName()+"--"+image.getIndex());
                     if(!augmentedImageMap.containsKey(image)) {
                         if (image.getName().equals("earth")) {
                             Anchor anchor = image.createAnchor(image.getCenterPose());
                             augmentedImageMap.put(image, anchor);
                             createMode(anchor);
                         }
                         if (image.getName().equals("ironman")) {
                             Anchor anchor = image.createAnchor(image.getCenterPose());
                             augmentedImageMap.put(image, anchor);
                             createIronmanMode(anchor);
                         }
                         if (image.getName().equals("penguin")) {
                             Anchor anchor = image.createAnchor(image.getCenterPose());
                             augmentedImageMap.put(image, anchor);
                             createPenguinMode(anchor);
                         }
                         return;
                     }
                     break;
                 case PAUSED:
                     Log.d("**Paused:","Inside Paused....."+image.getName()+"--"+image.getIndex());
                     augmentedImageMap.remove(image);
                     createNullMode();
                     break;
                 case STOPPED:
                     Log.d("**Stopped:","Inside Stopped....."+image.getName()+"--"+image.getIndex());
                     augmentedImageMap.remove(image);
                     break;
                 default:
                     // code block
             }

         }
     }
     private void createNullMode() {


     }

     private void createMode(Anchor anchor) {
         ModelRenderable.builder()
                 .setSource(this, Uri.parse("earth_obj.sfb"))
                 .build()
                 .thenAccept(modelRenderable -> placeModel(modelRenderable,anchor));
     }
     private void createIronmanMode(Anchor anchor){
         ModelRenderable.builder()
                 .setSource(this, Uri.parse("IronMan.sfb"))
                 .build()
                 .thenAccept(modelRenderable -> placeModel(modelRenderable,anchor));
     }
     private void createPenguinMode(Anchor anchor){
         ModelRenderable.builder()
                 .setSource(this, Uri.parse("PenguinBaseMesh.sfb"))
                 .build()
                 .thenAccept(modelRenderable -> placeModel(modelRenderable,anchor));
     }
     private void placeModel(ModelRenderable modelRenderable, Anchor anchor) {
         AnchorNode anchorNode = new AnchorNode(anchor);
         anchorNode.setRenderable(modelRenderable);
         arFragment.getArSceneView().getScene().addChild(anchorNode);
     }
 }
