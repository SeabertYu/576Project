__author__ = 'Boyang'
from math import *
from os import listdir
from PIL import Image
import operator

HEIGHT = 288
WIDTH = 352
LENGTH = HEIGHT * WIDTH
FOLDER = "./dataset/"
JPG_FOLDER = "./JPG/"

def printEntropy():
    counter = 1
    result = {}
    for file in listdir(FOLDER):
        entropy = getEntropy(file)
        #print(counter, " : ", entropy)
        result[counter] = entropy
        counter += 1
    sorted_d = sorted(result.items(), key=operator.itemgetter(1))
    print sorted_d


def getEntropy(file):
    file = open(FOLDER + file, 'rb')
    buffer = file.read()
    print(len(buffer))
    offset = 0
    histogram = [[0 for i in range(256)] for j in range(3)]
    for i in range(HEIGHT):
        for j in range(WIDTH):
            for k in range(3):
                b = ord(buffer[offset + LENGTH * k])
                histogram[k][b & 0xff] += 1
        offset += 1
    entropy = 0.0
    for k in range(3):
        for i in range(256):
            p = float(histogram[k][i]) / (LENGTH)
            if p != 0:
                entropy -= p * log(p, 2)
    return entropy

def getEntropyFromHistogram(file):
    image = Image.open(JPG_FOLDER+file).convert('L')#gray scale
    histogram = image.histogram()
    histogram_length = sum(histogram)

    samples_probability = [float(h) / histogram_length for h in histogram]

    return -sum([p * log(p, 2) for p in samples_probability if p != 0])

printEntropy()