__author__ = 'apple'
from key_frame_extract import match
from os import listdir
import cv2
from featureExtraction import cluster_image
from face_detect import detect
from key_frame_extract import get_all_imgs
import pickle
import eval
from featureExtraction import array2json
import json

HEAD_CLUSTER_ID = -2

# add new matchings to all existing matchings
def addimage(matching, exist_image, imagedir):
    new_len = len(matching) + len(listdir(imagedir))
    old_len = len(exist_image)
    new_matching = [[0 for x in range(new_len)] for x in range(new_len)]
    for i in range(len(matching)):
        for j in range(len(matching[i])):
            new_matching[i][j] = matching[i][j]
    new_images = [0 for x in range(len(listdir(imagedir)))]
    for fn in listdir(imagedir):
        if not fn[-3:] == "jpg":
            continue
        index = int(fn[-7:-4])
        afn = imagedir + "/" + fn
        image = cv2.imread(open(afn, "w"))
        new_images[index - 1 - old_len] = image

    for ni in range(len(new_images)):
        for i in range(len(exist_image)):
            new_matching[i][ni + old_len] = match(exist_image[i], new_images[ni])
        exist_image.append(new_images[ni])
    return new_matching

def cluster_new_image(old_matching_file, old_heads_file, old_image_dir, new_image_dir):
    cascPath = "/Users/apple/graduate/Courses/576 Multimedia/project/FaceDetect-master/haarcascade_frontalface_default.xml"
    all_heads = pickle.load(open(old_heads_file, "r"))
    old_matching = pickle.load(open(old_matching_file, "r"))
    faceCascade = cv2.CascadeClassifier(cascPath)
    heads = detect(new_image_dir, faceCascade, 1.1, 5)
    for nh in heads:
        all_heads.append(nh)
    exist_image = get_all_imgs(old_image_dir)
    matching = addimage(old_matching, exist_image, new_image_dir)
    image_cluster = cluster_h(all_heads, matching)
    return image_cluster

def cluster_h(heads, matching):
    new_len = len(matching) - len(heads)
    m = [[0 for x in range(new_len)] for x in range(new_len)]
    hdict = {}
    for h in heads:
        hdict[h] = True
    index = 0
    for i in range(len(matching)):
        if hdict.has_key(i) and hdict[i]:
            continue
        jndex = 0
        for j in range(len(matching)):
            if hdict.has_key(j) and hdict[j]:
                continue
            m[index][jndex] = matching[i][j]
            jndex += 1
        index += 1
    image_cluster = cluster_image(m, 44, 24)
    image_cluster = image_cluster.tolist()
    for h in heads:
        image_cluster.insert(h, -2)
    return image_cluster

def cluster_new_dateset():
    old_matching_file = "matching.txt"
    old_heads_file = "heads.txt"
    old_image_dir = "/Users/apple/graduate/Courses/576 Multimedia/workspace/ImageClustering/img/unclustered"
    new_image_dir = ""
    image_cluster = cluster_new_image(old_matching_file, old_heads_file, old_image_dir, new_image_dir)
    return image_cluster

if __name__ == "__main__":
    heads = pickle.load(open("heads.pickle", "r"))
    matching = pickle.load(open("matching.txt", "r"))
    ic = cluster_h(heads, matching)
    pickle.dump(ic, open("image_cluster.pickle", "w"))
    json.dump(array2json(ic), open("image_cluster.json", "w"))
    eval.eval_overall(ic, True)