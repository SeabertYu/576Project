import numpy as np
import cv2
from matplotlib import pyplot as plt
import sys
from os import listdir
from os.path import isfile
import pickle
from scipy.cluster.hierarchy import linkage
from scipy.cluster.hierarchy import fcluster
import eval


def drawKeypoint(fpath):
    img = cv2.imread(fpath,0)

    # Initiate STAR detector
    orb = cv2.ORB()

    # find the keypoints with ORB
    kp = orb.detect(img,None)

    # compute the descriptors with ORB
    kp, des = orb.compute(img, kp)

    # draw only keypoints location,not size and orientation
    img2 = cv2.drawKeypoints(img,kp,color=(0,255,0), flags=0)
    plt.imshow(img2),plt.show()

def match(f1, f2):
    print "match " + f1[-7:-3] + ", " + f2[-7:-3]
    img1 = cv2.imread(f1,0)          # queryImage
    img2 = cv2.imread(f2,0) # trainImage

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

    print kp1
    # img1 = cv2.drawKeypoints(img1, kp1)
    # cv2.imshow("img", img1)
    # cv2.waitKey(0)
    # img2 = cv2.drawKeypoints(img2, kp2)
    # cv2.imshow("img", img2)
    # cv2.waitKey(0)
    return s, len(kp1), len(kp2)

def compute(dirpath, out):
    imgs = []
    for f in listdir(dirpath):
        ff = dirpath + f
        if isfile(dirpath + f):
            imgs.append(ff)

    matching = [[0 for x in range(len(imgs))] for x in range(len(imgs))]
    for i in range(0, len(imgs)):
        if "jpg" != imgs[i][-3:]:
            continue
        index = int(imgs[i][-7:-4])
        for j in range(i+1, len(imgs)):
            jndex = int(imgs[j][-7:-4])
            matching[index-1][jndex-1] = match(imgs[i], imgs[j])

    pickle.dump(matching, open(out, "wb+"))

def cluster(matching, threshold, ratio, out):
    result = {}
    k = 30
    clz = 0
    visited = {}
    for i in range(0, len(matching) - 1):
        if visited.has_key(i) and visited[i] == True:
            continue
        if result.has_key(clz) == False:
            result[clz] = []
            result[clz].append(i+1)
        for j in range(i+1, len(matching) - 1):
            if visited.has_key(j) and visited[j] == True:
                continue
            r, kp1, kp2 = matching[i][j]
            contains = False
            if r > threshold:
                contains = True
            else:
                r1 = r / kp1
                r2 = r / kp2
                if r1 > ratio or r2 > ratio:
                    contains = True
            if contains == True:
                result[clz].append(j+1)
                visited[j] = True
        clz += 1
    print clz
    print result
    pickle.dump(result, open(out, "wb+"))

def naiveProcess(matching, k):
    n = [[0.0 for x in range(0, len(matching) - 1)] for x in range(0, len(matching) - 1)]
    for i in range(0, len(matching) - 1):
        for j in range(i+1, len(matching) - 1):
            m,x,y = matching[i][j]
            if m >= k:
                n[i][j] = float(1 / float(m))
            else:
                n[i][j] = 1

    return n

def main(matching, logging):
    min_i = -1
    min_k = -1
    min_m = 99999
    for i in range(15, 50):
        print "max clusters", i
        for k in range(5, 50):
            e,f = cluster(matching, i, k, logging)
            if f < min_m:
                min_m = f
                min_i = i
                min_k = k
    print min_i, min_k
    cluster(matching, min_i, min_k, True)
    return min_i, min_k, min_m

def cluster(matching, i, k, logging):
    x = naiveProcess(matching, k)
    z = linkage(x,method='complete')
    clz = fcluster(z, i, criterion='maxclust')
    e,f = eval.eval_overall(clz, logging)
    return e,f

if __name__ == "__main__":
    # dirpath = "/Users/apple/graduate/Courses/576 Multimedia/workspace/ImageClustering/img/unclustered/"
    out = "matching.txt"
    matching = pickle.load(open(out, "rb"))
    #main(matching, False)
    cluster(matching, 44, 24, True)
