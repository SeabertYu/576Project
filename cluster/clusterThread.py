__author__ = 'apple'

import threading
import Queue
from matchThread import matchThread

class clusterThread (threading.Thread):
    def __init__(self, clusterId, all_img, frames,clz2ImgIndex, candidate, num, lock, r):
        threading.Thread.__init__(self)
        self.clusterId = clusterId
        self.candidate = candidate
        self.clz2ImgIndex = clz2ImgIndex
        self.all_img = all_img
        self.frames = frames
        self.num = num
        self.lock = lock
        self.r = r

    def run(self):
        inds = self.clz2ImgIndex[self.clusterId]
        score = 0.0
        ind50 = {}
        # compare all frames to all images in a cluster
        for frameindex in range(len(self.frames)):
            temp = 0.0
            # go through all images in a cluster
            threads = []
            workQueue = {}
            queueLock = threading.Lock()
            for indi in range(len(inds)):
                t = matchThread(indi, self.all_img[inds[indi]], self.frames[frameindex], workQueue, queueLock)
                t.start()
                threads.append(t)
            while not len(workQueue) == len(inds):
                pass
            for imgindex in workQueue.keys():
                m, k1, k2 = workQueue[imgindex]
                if m >= self.r:
                    ind50[frameindex] = True
                temp += m
            score += float(temp) / float(len(inds))
            for t in threads:
                t.join()
        if len(ind50) > len(self.frames) / 2:
            # add to candidate cluster
            self.lock.acquire()
            print "add cluster ",self.clusterId, score
            self.candidate[self.clusterId] = float(float(score) / float(len(self.frames)))
            self.lock.release()
        self.lock.acquire()
        print "finish matching for cluster ", self.clusterId
        self.num.append(self.clusterId)
        self.lock.release()