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
import eval
import json
from featureExtraction import array2json

def get_all(dirpath):
    s = len(listdir(dirpath))
    imgs = [0 for x in range(s)]
    for f in listdir(dirpath):
        ff = dirpath + "/" + f
        index = int(f[:-4])
        img = cv2.imread(ff)
        imgs[index] = img
    return imgs

def get_all_imgs(dirpath):
    s = len(listdir(dirpath))
    imgs = [0 for x in range(s)]
    for f in listdir(dirpath):
        ff = dirpath + "/" + f
        if f[-3:] == "jpg":
            index = int(f[-7:-4])
            img = cv2.imread(ff)
            imgs[index - 1] = img
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

def expand(index,mlen, incre_s):
    new_index = [i for i in index]
    if new_index[0] != 0:
        new_index.insert(0, 0)
    if new_index[-1] != mlen:
        new_index.insert(len(new_index), mlen)
    for i in range(0, incre_s):
        d = []
        pre = 0
        for j in new_index:
            d.append(j - pre)
            pre = j
        if index[-1] != mlen:
            d.append(mlen - new_index[-1])
        max = 0
        max_i = 0
        j = 0
        for em in d:
            if em >= max:
                max = em
                max_i = j
            j += 1
        # expand
        if max_i == 0:
            new_i = new_index[0] / 2
        else:
            new_i = (new_index[max_i] + new_index[max_i-1]) / 2
        new_index.insert(max_i, new_i)

    return new_index

def clz2dic(clz2ImgIndex):
    c = {}
    for i in range(len(clz2ImgIndex)):
        if c.has_key(clz2ImgIndex[i]) == False:
            c[clz2ImgIndex[i]] = []
        c[clz2ImgIndex[i]].append(i)
    return c

def main():
    clz = pickle.load(open("cluster.txt", "r"))
    basedir = "/Users/apple/graduate/Courses/576 Multimedia/workspace/ImageClustering/frame/"
    imgsdir = "/Users/apple/graduate/Courses/576 Multimedia/workspace/ImageClustering/img/unclustered"
    kframes = preprocess(build_k_frames())
    clz2ImgIndex = clz2dic(clz)
    all_img = get_all_imgs(imgsdir)
    video_cluster = [0 for i in range(10)]
    for i in range(1, 11):
        print "cmp video " + str(i)
        d = basedir + str(i)
        fi = kframes[i]
        frames = [cv2.imread(d + "/" + str(ffi) + ".jpg") for ffi in fi]
        max_score = 0.0
        max_cluster_id = -1
        for clz in clz2ImgIndex.keys():
            inds = clz2ImgIndex[clz]
            score = 0.0
            for ind in inds:
                temp = 0
                for frame in frames:
                    m, k1, k2 = match(all_img[ind], frame)
                    temp += m
                score += temp / len(frames)
            if score / len(inds) > max_score:
                max_score = score / len(inds)
                max_cluster_id = clz
        video_cluster[i-1] = max_cluster_id
        print "video " + str(i) + " belong to " + str(max_cluster_id) + " with score " + str(max_score)
    return video_cluster

def build_k_frames():
    kframes = {}
    kframes[1] = [0, 231]
    kframes[2] = [0, 173]
    kframes[3] = [0, 282]
    kframes[4] = [0, 257]
    kframes[5] = [0, 102, 214]
    kframes[6] = [0, 258]
    kframes[7] = [0]
    kframes[8] = [0, 272]
    kframes[9] = [0]
    kframes[10] = [0]
    return kframes

def preprocess(kframes):
    newkframes = {}
    for k in kframes.keys():
        a = expand(kframes[k], 299, 1)
        newkframes[k] = a
    return newkframes

if __name__  == "__main__":
    video_cluster = pickle.load(open("video_cluster.txt", "r"))
    pickle.dump(video_cluster, open("video_cluster.pickle", "w+"))
    json.dump(array2json(video_cluster), open("video_cluster.json", "w+"))
    clz = pickle.load(open("image_cluster.pickle", "r"))
    eval.eval_video(video_cluster, clz2dic(clz))