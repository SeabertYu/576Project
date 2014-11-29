__author__ = 'haoyu'

def eval_video(video_cluster, cluster2ImgIndex):
    index = build()
    vd = {}
    cvd = build_video()
    for i in range(len(video_cluster)):
        vc = video_cluster[i]
        imginds = cluster2ImgIndex[int(vc)]
        vd[i] = {}
        for ind in imginds:
            vd[i][index[ind]] = True

    correct = 0
    for vdkey in vd.keys():
        a = cvd[vdkey]
        for akey in a.keys():
            if vd[vdkey].has_key(akey):
                correct += 1
                break
    print correct
    return correct


def build_video():
    vd = [{} for i in range(10)]
    vd[0]["unknown"] = True
    vd[1]["leavy"] = True
    vd[1]["red building"] = True
    vd[2]["leavy"] = True
    vd[3]["leavy"] = True
    vd[4]["unknown"] = True
    vd[5]["dohney"] = True
    vd[6]["circle"] = True
    vd[6]["dohney"] = True
    vd[7]["unknown"] = True
    vd[8]["leavy"] = True
    vd[9]["circle"] = True
    vd[9]["tommy"] = True
    return vd


def eval_label(label, inds, logging):
    index = build()
    precision = 0.0
    recall = 0.0
    c = 0
    l = 0
    for i in index:
        if i == label:
            l += 1
    for h in inds:
        if index[h-1] == label:
            c += 1
    precision = float(c) / len(inds)
    recall = float(c) / l
    if logging:
        print precision, recall
    return 2 * precision * recall / (precision + recall)

def build():
    index = ["" for i in range(300)]
    domains = ["tommy", "dohney", "leavy", "SAL", "venue", "seat", "cal_pizza_kitchen", "tutor center", "windows", "red building", "arc", "circle", "head", "comic"]
    d = 0
    # tommy
    for i in range(60, 79):
        index[i] = domains[d]
    index[146] = domains[d]
    d = 1
    # dohney
    for i in range(79, 97):
        index[i] = domains[d]
    index[149] = domains[d]
    for i in range(212, 224):
        index[i] = domains[d]
    d = 2
    # leavy
    for i in range(97, 113):
        index[i] = domains[d]
    index[147] = domains[d]
    for i in range(177, 196):
        index[i] = domains[d]
    for i in range(263, 275):
        index[i] = domains[d]
    d = 3
    #SAL
    for i in range(113, 126):
        index[i] = domains[d]
    d = 4
    # venue
    for i in range(126, 146):
        index[i] = domains[d]
    d = 5
    # seat
    for i in range(150, 164):
        index[i] = domains[d]
    for i in range(224, 230):
        index[i] = domains[d]
    d = 6
    # pizza
    for i in range(164, 177):
        index[i] = domains[d]
    d = 7
    # tutor
    for i in range(196, 212):
        index[i] = domains[d]
    d = 8
    # windows
    for i in range(231, 251):
        index[i] = domains[d]
    d = 9
    # red building
    for i in range(251, 263):
        index[i] = domains[d]
    # not sure which class should these two image be in
    index[298] = domains[d]
    index[299] = domains[d]
    d = 10
    # arc
    for i in range(275, 292):
        index[i] = domains[d]
    d = 11
    # circle
    for i in range(292, 298):
        index[i] = domains[d]
    # head
    d = 12
    index[30] = domains[d]
    index[40] = domains[d]
    for i in range(43, 49):
        index[i] = domains[d]
    for i in range(50, 57):
        index[i] = domains[d]
    index[58] = domains[d]
    index[59] = domains[d]
    # comic
    d = 13
    for i in range(20, 40):
        index[i] = domains[d]
    index[49] = domains[d]
    index[57] = domains[d]
    return index


def eval_overall(cc, logging):
    if logging:
        print "domain,", "number of clusters,", "number of negative images"
    index = build()
    domains = {}
    f = 0
    ff = 0
    for i in range(len(index)):
        if "" == index[i] or domains.has_key(index[i]):
            continue;
        domains[index[i]] = []
        for j in range(len(index)):
            if index[i] == index[j]:
                domains[index[i]].append(j)
    for d in domains.keys():
        imgs, cluster, false = eval(cc, domains[d], d, logging)
        f += 2 * cluster + false
        ff += false

    return f, ff



#cc:[](cluster result), index:[](correct cluster), domain:str, logging:bool
def eval(cc, index, domain, logging):
    a = {}
    falseclassified = 0
    d = {}
    for i in index:
        d[i] = True
    # find out how many clusters have in cluster result.
    for i in index:
        a[cc[i]] = True
    #print a
    # find out how many image should not be clustered into this cluster
    for i in range(0, len(cc)):
        if a.has_key(cc[i]):
            if d.has_key(i) == False:
                falseclassified += 1

    if logging:
        print str(domain), len(index), len(a), falseclassified
    return len(index), len(a), falseclassified


