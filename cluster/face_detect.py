import cv2
import sys
from os import listdir
from os.path import isfile
import json

# Get user supplied values

def detect(imagePath):
    cascPath = "/Users/apple/graduate/Courses/576 Multimedia/project/FaceDetect-master/haarcascade_frontalface_default.xml"
    heads = []
    # Create the haar cascade
    faceCascade = cv2.CascadeClassifier(cascPath)

    for f in listdir(imagePath):
        im = imagePath + "/" + f
        if detectf(im, faceCascade):
            heads.append(f)


    print "heads" + str(len(heads))
    return heads

def detectf(f, faceCascade):
    image = cv2.imread(f)
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    # Detect faces in the image
    faces = faceCascade.detectMultiScale(
        gray,
        scaleFactor=1.1,
        minNeighbors=5,
        minSize=(30, 30),
        flags = cv2.cv.CV_HAAR_SCALE_IMAGE
    )
    if len(faces):
        return True

	# Draw a rectangle around the faces
	#for (x, y, w, h) in faces:
	#	cv2.rectangle(image, (x, y), (x+w, y+h), (0, 255, 0), 2)

if __name__ == '__main__':
    cascPath = "/Users/apple/graduate/Courses/576 Multimedia/project/FaceDetect-master/haarcascade_frontalface_default.xml"

    # Create the haar cascade
    #faceCascade = cv2.CascadeClassifier(cascPath)
    #heads = detect("/Users/apple/graduate/Courses/576 Multimedia/workspace/ImageClustering/img/unclustered/image034.jpg", faceCascade)
    heads = detect("/Users/apple/graduate/Courses/576 Multimedia/workspace/ImageClustering/img/unclustered")
    data = {}
    data["head"] = heads

    outfile = open('heads.txt', 'w+')
    outfile.write(json.dumps(data))