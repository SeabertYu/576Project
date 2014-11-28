import cv2
import sys
from os import listdir
from os.path import isfile
import json
import eval

# Get user supplied values

def drange(start, stop, step):
    r = start
    while r < stop:
        yield r
        r += step

def detect(imagePath,faceCascade,s,n):
    heads = []
    for f in listdir(imagePath):
        if "jpg" != f[-3:]:
            continue
        im = imagePath + "/" + f
        if detectf(im, faceCascade,scale=s, minN=n):
            heads.append(int(f[-7:-4]) - 1)
    return heads

def detectf(f, faceCascade, scale=1.1, minN=5, minS=(30, 30)):
    image = cv2.imread(f)
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    # Detect faces in the image
    faces = faceCascade.detectMultiScale(
        gray,
        scaleFactor=scale,
        minNeighbors=minN,
        minSize=minS,
        flags = cv2.cv.CV_HAAR_SCALE_IMAGE
    )
    if len(faces):
        return True

	# Draw a rectangle around the faces
	#for (x, y, w, h) in faces:
	#	cv2.rectangle(image, (x, y), (x+w, y+h), (0, 255, 0), 2)

def main():
    maxf = 0
    maxS = 1.0
    maxN = 1
    dir = "/Users/apple/graduate/Courses/576 Multimedia/workspace/ImageClustering/img/unclustered"
    cc = [0 for x in range(0, 300)]
    h = []
    cascPath = "/Users/apple/graduate/Courses/576 Multimedia/project/FaceDetect-master/haarcascade_frontalface_default.xml"
    # Create the haar cascade
    faceCascade = cv2.CascadeClassifier(cascPath)
    for s in drange(1.1, 3, 0.1):
        print "scale factor ", s
        for n in range(1, 10, 1):
            heads = detect(dir,faceCascade, s, n)
            f = eval.eval_head(heads, False)
            if f > maxf:
                maxf = f
                maxS = s
                maxN = n
                h = heads

    print h
    print maxf, maxS, maxN

if __name__ == '__main__':
    cascPath = "/Users/apple/graduate/Courses/576 Multimedia/project/FaceDetect-master/haarcascade_frontalface_default.xml"
    main()
