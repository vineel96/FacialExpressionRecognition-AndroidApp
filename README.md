# FacialExpressionRecognition-AndroidApp

An Android Application which dynamically detects expressions on human face.
<br /><br/>
Dependencies used in this project are:
<br/><br/>
1.Tensorflow_Inference Libraries which provides API's to access Tensorflow saved models and also to get output from it.<br />
2.Android Google Vision FaceDetection Dependencies which is used to detect the face region in the input image from camera.<br />
3.Pretrained Model is kept in assets folder.Its a big size file which contains the CNN model.
<br /><br />
Assets file and tensorflow libraries are not uploaded since they are very large in size.
<br /><br />
Working:
<br /><br />
      User is asked to take a picture from the device camera. It is then checked if face part is present or not in the image. If face  region is detected then image is cropped to contain only the facepart. It is then converted to grayscale and resized to 48x48 image. Then this image is resized again to -1x2304 which is the size expected by the CNN model. Then it Outputs an array consisting of  probablities of each expression in array of size 7.(Since 7 expressions are present in total)
      <br /><br />
       Asynchronous Tasks are used since loading of tensorflow libs and pretrained model takes lot of time.Using AsyncTask UI is made responsive.
       
