__author__ = 'haoyu'

def build():
    index = ["" for i in range(300)]
    domains = ["tommy", "dohney", "leavy", "SAL", "venue", "seat", "cal_pizza_kitchen", "tutor center", "windows", "red building", "arc", "circle"]
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
    return index


def eval_overall(cc, logging):
    if logging:
        print "domain,", "number of clusters,", "number of negative images"
    index = build()
    domains = {}
    f = 0
    for i in range(len(index)):
        if "" == index[i] or domains.has_key(index[i]):
            continue;
        domains[index[i]] = []
        for j in range(len(index)):
            if index[i] == index[j]:
                domains[index[i]].append(j)
    for d in domains.keys():
        cluster, false = eval(cc, domains[d], d, logging)
        f += 2 * cluster + false

    return f



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
    # find out how many image should not be clustered into this cluster
    for i in range(0, len(cc)):
        if a.has_key(cc[i]):
            if d.has_key(i) == False:
                falseclassified += 1

    if logging:
        print str(domain), len(a), falseclassified
    return len(a), falseclassified
