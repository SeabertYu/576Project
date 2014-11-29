__author__ = 'apple'

import threading
import Queue
import cv2

class matchThread (threading.Thread):
    def __init__(self, frameindex,img,frame, q, lock):
        threading.Thread.__init__(self)
        self.img = img
        self.frame = frame
        self.frameindex = frameindex
        self.q = q
        self.lock = lock

    def run(self):
        result = match(self.img, self.frame)
        self.lock.acquire()
        self.q[self.frameindex] = result
        self.lock.release()

def match(img1, img2):

    # Initiate SIFT detector
    sift = cv2.SIFT()

    # find the keypoints and descriptors with SIFT
    kp1, des1 = sift.detectAndCompute(img1,None)
    kp2, des2 = sift.detectAndCompute(img2,None)

    # FLANN parameters
    FLANN_INDEX_KDTREE = 0
    index_params = dict(algorithm = FLANN_INDEX_KDTREE, trees = 5)
    search_params = dict(checks=50)   # or pass empty dictionary

    bf = cv2.BFMatcher()
    matches = bf.knnMatch(des1,des2, k=2)

    # Need to draw only good matches, so create a mask
    s = 0
    for i,(m,n) in enumerate(matches):
        if m.distance < 0.7*n.distance:
            s+=1

    return s, len(kp1), len(kp2)