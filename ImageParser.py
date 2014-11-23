__author__ = 'Boyang'
from math import *
from os import listdir

HEIGHT = 288
WIDTH = 352
LENGTH = HEIGHT * WIDTH
FOLDER = "./dataset/"


def printEntropy():
    counter = 1
    for file in listdir(FOLDER):
        print(counter, " : ", getEntropy(file))
        counter += 1


def getEntropy(file):
    file = open(FOLDER + file, 'rb')
    buffer = file.read()
    offset = 0
    histogram = [[0 for i in range(256)] for j in range(3)]
    for i in range(HEIGHT):
        for j in range(WIDTH):
            for k in range(3):
                b = buffer[offset + LENGTH * k]
                histogram[k][b & 0xff] += 1
        offset += 1
    entropy = 0.0
    for k in range(3):
        for i in range(256):
            p = float(histogram[k][i]) / (LENGTH*3)
            if p != 0:
                entropy -= p * log(p, 2)
    return entropy


printEntropy()