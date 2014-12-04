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
import threading
from clusterThread import clusterThread

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

def matchWithImageIndex(imgs, i, j):
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
            s, k1, k2 = matchWithImageIndex(imgs, i, j)
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
    if incre_s == 0:
        return new_index
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

# expand key frames to k frames, compare all key frames to all clusters, if > 50% frames in a video matches > r features in a cluster
# , choose the cluster with the maximum average score among them, otherwise, label it as unknown.
def main(mlen, k, r):
    clz = pickle.load(open("image_cluster.pickle", "r"))
    basedir = "/Users/apple/graduate/Courses/576 Multimedia/workspace/ImageClustering/frame/"
    imgsdir = "/Users/apple/graduate/Courses/576 Multimedia/workspace/ImageClustering/img/unclustered"
    kframes = preprocess(build_k_frames(), mlen, k)
    clz2ImgIndex = clz2dic(clz)
    all_img = get_all_imgs(imgsdir)
    video_cluster = [0 for i in range(10)]
    for i in range(1, 11):
        print "cmp video " + str(i)
        d = basedir + str(i)
        fi = kframes[i]
        frames = [cv2.imread(d + "/" + str(ffi) + ".jpg") for ffi in fi]

        # go through all clusters
        candidates = {}
        threads = []
        clusters = []
        lock = threading.Lock()
        for clusterId in clz2ImgIndex.keys():
            t = clusterThread(clusterId, all_img, frames,clz2ImgIndex, candidates, clusters, lock,r)
            t.start()
            threads.append(t)
        print "created threads ", len(threads)
        while not len(clusters) == len(clz2ImgIndex):
            pass
        print " get response ", len(clz2ImgIndex)
        if len(candidates) == 0:
            video_cluster[i-1] = -1
            print "video " + str(i) + " type is unknown"
        else:
            max_score = -1
            max_cluster_id = -1
            for cand in candidates.keys():
                if candidates[cand] > max_score:
                    max_score = candidates[cand]
                    max_cluster_id = cand
            video_cluster[i-1] = max_cluster_id
            print "video " + str(i) + " belong to " + str(max_cluster_id) + " with score " + str(max_score)

        for t in threads:
            t.join()

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

def preprocess(kframes, mlen, k):
    newkframes = {}
    for k in kframes.keys():
        a = expand(kframes[k], mlen, k)
        newkframes[k] = a
    return newkframes

def findbest():
    clz = pickle.load(open("image_cluster.pickle", "r"))
    maxc = -1
    maxinc = -1
    maxr = -1
    max_video_cluster = []
    for inc in range(0, 10):
        for r in range(10, 30):
            print "increase key frames size ", inc, " matching threshold ", r
            video_cluster = main(299, inc, r)
            c = eval.eval_video(video_cluster, clz2dic(clz))
            if c > maxc:
                maxc = c
                maxinc = inc
                maxr = r
                max_video_cluster = [video_cluster[i] for i in range(len(video_cluster))]
    pickle.dump(max_video_cluster, open("video_cluster.pickle", "w+"))
    json.dump(array2json(max_video_cluster), open("video_cluster.json", "w+"))

def naive_main():
    clz = pickle.load(open("image_cluster.pickle", "r"))
    basedir = "/Users/apple/graduate/Courses/576 Multimedia/workspace/ImageClustering/frame/"
    imgsdir = "/Users/apple/graduate/Courses/576 Multimedia/workspace/ImageClustering/img/unclustered"
    kframes = preprocess(build_k_frames(), 299, 1)
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
    return video_cluster, clz2ImgIndex

if __name__  == "__main__":
    # img1 = cv2.imread("/Users/apple/graduate/Courses/576 Multimedia/workspace/ImageClustering/frame/1/0.jpg")
    # img2 = cv2.imread("/Users/apple/graduate/Courses/576 Multimedia/workspace/ImageClustering/img/unclustered/image009.jpg")
    # print match(img1,img2)
    # findbest()
    video_cluster, image_cluster = naive_main()
    eval.eval_video(video_cluster, image_cluster)
