import cv2
import numpy as np
from matplotlib import pyplot as plt
import math
from os import listdir
import operator
import eval

def blur(path):
    img = cv2.imread(path)
    kernel = np.ones((5,5),np.float32)/25
    dst = cv2.filter2D(img,-1,kernel)
    kernel = np.ones((5,5),np.float32)/25
    dst = cv2.filter2D(img,-1,kernel)
    return img, dst

def diff_rgb(img1, img2):
    d = 0
    for i in range(len(img1)):
        for j in range(len(img1[i])):
            l1 = img1[i][j].tolist()
            l2 = img2[i][j].tolist()
            for k in range(len(l1)):
                d += abs(l2[k] - l1[k])
    return d

def main():
    imgdir = "/Users/apple/graduate/Courses/576 Multimedia/workspace/ImageClustering/img/unclustered/"
    d = {}
    for f in listdir(imgdir):
        if f[-3:] == "jpg":
            img, dst = blur(imgdir + f)
            index = int(f[-7:-4])
            d[index - 1] = diff_rgb(img, dst)
    sorted_d = sorted(d.items(), key=operator.itemgetter(1))
    print sorted_d
    inds = [sorted_d[i][0] for i in range(0,22)]
    eval.eval_label("comic", inds, True)

if __name__ == "__main__":
    main()
