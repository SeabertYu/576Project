__author__ = 'apple'

import numpy as np
import cv2
from matplotlib import pyplot as plt
import sys
from os import listdir
from os.path import isfile
import pickle
from scipy.cluster.hierarchy import linkage
from scipy.cluster.hierarchy import fcluster
import featureExtraction

def get_all(dirpath):
    s = len(listdir(dirpath))
    imgs = [0 for x in range(s)]
    for f in listdir(dirpath):
        ff = dirpath + "/" + f
        index = int(f[:-4])
        img = cv2.imread(ff)
        imgs[index] = img
    return imgs

def match(imgs, i, j):
    print "match ", i, j
    img1 = imgs[i]          # queryImage
    img2 = imgs[j]

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

def get_key_frames(dirpath, m, r):
    imgs = get_all(dirpath)
    kframes = []
    i = 0
    while i < len(imgs) - 1:
        kframes.append(i)
        for j in range(i+1, len(imgs)):
            s, k1, k2 = match(imgs, i, j)
            if s >= m:
                continue
            elif float(s) / float(k1) >= r or float(s) / float(k2) >= r:
                continue
            else:
                break
        i = j
    return imgs, kframes

def load():
    dirpath = "/Users/apple/graduate/Courses/576 Multimedia/workspace/ImageClustering/frame/10"
    imgs, kframes = get_key_frames(dirpath, 15, 0.2)
    print kframes
    print len(kframes)
    for i in kframes:
        cv2.imshow("kframes", imgs[i])
        cv2.waitKey(0)

def expand(index,len, incre_s):
    new_index = [i for i in index]
    for i in range(0, incre_s):
        d = []
        pre = 0
        for j in new_index:
            d.append(j - pre)
            pre = j
        if index[-1] != len:
            d.append(len - new_index[-1])
        max = 0
        max_i = 0
        j = 0
        for em in d:
            if em >= max:
                max = em
                max_i = j
            j += 1
        # expand
        new_i = (new_index[max_i] + new_index[max_i-1]) / 2
        if max_i == 0:
            new_i = new_index[0] / 2
        new_index.insert(max_i, new_i)

    return new_index

if __name__  == "__main__":
    index = [0, 248]
    print expand(index, 300, 5)