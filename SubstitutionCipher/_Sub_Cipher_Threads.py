import time
import collections
import threading
import sys
import os
from multiprocessing import Lock, Process, Queue, current_process
s = ""
# decrypted text
e = 0
two_common = ["to", "in", "is","it", "be", "as", "at", "so", "we",
"he", "or", "on", "do","my", "an", "go",
"no","us"] # most common two letter words

three_common = ["the" , "and", "for", "but", "him"
"had", "her", "was", "one","get","how","see","two","who","its","let","put","use"] # most common three letter words

four_common = ["that", "with", "have", "this", "will","from"] # most common four letter words

com_3_doubles = ["all", "see", "off", "too"]
# common 3 letter words with double letters in them

com_4_doubles = ["will", "well"]
# common 4 letter words with double letters in them


MCL = ""
MCLH = ""
one = {}
two = {}
three = {}
four = {}
Awords = {}
alph_keys = {"a":["a"],"b":["b"],"c":["c"],"d":["d"],"e":["e"],"f":["f"],"g":["g"],"h":["h"],
"i":["i"],"j":["j"],"k":["k"],"l":["l"],"m":["m"],"n":["n"],"o":["o"],"p":["p"],"q":["q"],
"r":["r"],"s":["s"],"t":["t"],"u":["u"],"v":["v"],"w":["w"],"x":["x"],"y":["y"],"z":["z"]}
# decrypted letter first then the encrypted one after.
alph_keys_1 = {"a":["a"],"b":["b"],"c":["c"],"d":["d"],"e":["e"],"f":["f"],"g":["g"],"h":["h"],
"i":["i"],"j":["j"],"k":["k"],"l":["l"],"m":["m"],"n":["n"],"o":["o"],"p":["p"],"q":["q"],
"r":["r"],"s":["s"],"t":["t"],"u":["u"],"v":["v"],"w":["w"],"x":["x"],"y":["y"],"z":["z"]}
#encrypted letter first then decryped after
q = ""
# get the most common letter from cipher
enc = [] # all the encrypted letters you have done so far
dec = [] # all the decrypted letters you have done so far
letters = [] # all the letters that come up in this text
# determine how much letters you do not need to decrypt.
original_time = 0 # timer for when the script starts
end_time = 0 # timer for when the script ends
original_time = time.time()
file_name = ""

#open file and read its contents;
file_name =  sys.argv[1]
file_name = file_name.replace(".txt", "")
f = open(sys.argv[1])
fi = f.read()
f.close()
s = fi


def first_function():
    ae = 0
    strx = ""
    while(ae < len(s)):
        if(s[ae] != " "):
            strx += s[ae]
        ae += 1
    # get the most common letter from text and assign it to E
    MCLH = collections.Counter(strx).most_common(1)[0]
    MCL = MCLH[0]
    alph_keys[MCL][0] = "e"
    alph_keys_1["e"][0] = MCL
    enc.append("e")
    dec.append(MCL)

    # not get all the one,two,three and four letter words
    i = 0
    q = ""
    while(i < len(s)):
        if(s[i] != " "):
            if(s[i].isalpha()):
                if(s[i].isupper() and not s[i - 1].isalpha() and not s[i + 1].isalpha()): # if its upper will be i
                    q += s[i]
                else:
                    q += s[i].lower()
                if(s[i].lower() not in letters):
                    letters.append(s[i].lower())
        if(s[i] == " " or not s[i].isalpha):
            if(q != ""):
                Awords[q] = 1
            if(len(q) == 1):
                if(q not in one.keys()):
                    one[q] = 1
                else:
                    one[q] += 1
            if(len(q) == 2):
                if(q not in two.keys()):
                    two[q] = 1
                else:
                    two[q] += 1
            if(len(q) == 3):
                if(q not in three.keys()):
                    three[q] = 1
                else:
                    three[q] += 1
            if(len(q) == 4):
                if(q not in four.keys()):
                    four[q] = 1
                else:
                    four[q] += 1
            q = ""
        i += 1

    # get (the) and assign
    # will be most common 3 letter word
    ve = ""
    ve = max(three, key= three.get)
    hw = ""
    wt = ""
    if(ve[2] == MCL):
        alph_keys[ve[0]][0] = "t"
        alph_keys[ve[1]][0] = "h"
        alph_keys_1["h"][0] = ve[1]
        alph_keys_1["t"][0] = ve[0]
        enc.append("t")
        dec.append(ve[0])
        enc.append("h")
        dec.append(ve[1])
    else:
        for wt in three:
            if(wt[2] == MCL):
                alph_keys[ve[0]][0] = "t"
                alph_keys[ve[1]][0] = "h"
                alph_keys_1["h"][0] = ve[1]
                alph_keys_1["t"][0] = ve[0]
                enc.append("t")
                dec.append(ve[0])
                enc.append("h")
                dec.append(ve[1])
    # get the letter a or i by looking at words with one letter in them
    dw = ""
    dw = max(one, key= one.get)
    if(dw.isupper()):
        dcx = dw.lower()
        alph_keys[dcx][0] = "i"
        alph_keys_1["i"][0] = dcx
        enc.append("i")
        dec.append(dcx)
        one[dw] = 0
        sil = ""
        sil = max(one, key= one.get)
        alph_keys[sil][0] = "a"
        alph_keys_1["a"][0] = sil
        enc.append("a")
        dec.append(sil)
    else:
        alph_keys[dw][0] = "a"
        alph_keys_1["a"][0] =dw[0]
        enc.append("a")
        dec.append(dw)

# get letters by looking at the two letter words
def check_bigrams():
    holo = 0
    wq = ""
    TW = [] # array to hold all words with letters(a,e,t,h)
    # get all the two letter words which have letters you have decrypted
    for wq in two:
        lov = 0
        while(lov < len(dec)):
            if(dec[lov] in wq):
                TW.append(wq)
            lov += 1
    while(holo < len(TW)):
        sz = 0
        let = ""
        hed = ""
        # look at the first letter and compare too see if it has been decrypted yet
        # also check if it compares to any of the letters of the two_common array
        if(TW[holo][0] in dec):
            hed = TW[holo][0]
            ha = [] # holder array
            qa = 0
            # now check if TW array has any common letters with two_common array
            while(qa < len(two_common)):
                zp = ""
                zp = two_common[qa]
                ew = 0
                xp = dec.index(hed)
                kx = enc[xp] # kx is the encrypted version of our original two letter word
                # if common letter assign other letter in TW array
                # to other letter in two_common array
                if(zp[0] == kx):
                    if(zp[1] not in enc):
                        if(TW[holo][1] not in dec):
                            alph_keys[TW[holo][1]].append(zp[1])
                if(zp[1] == kx):
                    if(zp[0] not in enc):
                        if(TW[holo][0] not in dec):
                            alph_keys[TW[holo][0]].append(zp[0])
                qa += 1
        # now do the same but in this case it will be the second letters
        if(TW[holo][1] in dec):
            hed = TW[holo][1]
            ha = [] # holder array
            qa = 0
            # get all the words with that letter at start of them in two_common by loop
            while(qa < len(two_common)):
                zp = ""
                zp = two_common[qa]
                ew = 0
                xp = dec.index(hed)
                kx = enc[xp]
                # if common letter assign other letter in TW array
                # to other letter in two_common array
                if(zp[0] == kx):
                    if(zp[1] not in enc):
                        if(TW[holo][1] not in dec):
                            alph_keys[TW[holo][1]].append(zp[1])
                if(zp[1] == kx):
                    if(zp[0] not in enc):
                        if(TW[holo][0] not in dec):
                            alph_keys[TW[holo][0]].append(zp[0])
                qa += 1
        holo += 1
# now check all the options in the keys dictionary
# if there is only one value in array we can assign it.
def check_options():
    opt = ""
    for key,value in alph_keys.items():
        if(len(value) == 2):
            if(value[0] == key):
                value.pop(0)
                alph_keys_1[value[0]][0] = key
                enc.append(value[0])
                dec.append(key)

def findand():
    # find n and d by finding most common 3 letter word(and)
    andd = {}
    st = ""
    for key,value in three.items():
        st = alph_keys_1["a"]
        if(key[0] == st[0]):
            andd[key] = value
    wc = [] 
    wc.append(max(andd, key= andd.get))
    alph_keys[wc[0][1]] = ["n"]
    alph_keys[wc[0][2]] = ["d"]
    alph_keys_1["n"][0] = wc[0][1]
    alph_keys_1["d"][0] = wc[0][2]
    enc.append("n")
    dec.append(wc[0][1])
    enc.append("d")
    dec.append(wc[0][2])




def check_trigrams():
    # get all three letter words that contain letters you have rn
    wq = ""
    TR = []
    holo = 0
    for wq in three:
        ox = 0
        while(ox < len(dec)):
            if(dec[ox] in wq):
                TR.append(wq)
            ox += 1
    while(holo < len(TR)):
        sz = 0
        let = ""
        hed = ""
        hed1 = ""
        hed2 = ""
        # this is only the case for the first letter not the second one
        # this is only the case for the first letter not the second one
        if(TR[holo][0] in dec):
            if(TR[holo][1] in dec):
                hed = TR[holo][0]
                hed1 = TR[holo][1]
                ha = [] # holder array
                qa = 0
                # get all the words with that letter at start of them in two_common by loop
                while(qa < len(three_common)):
                    zp = ""
                    zp = three_common[qa]
                    ew = 0
                    xp = dec.index(hed)
                    xp1 = dec.index(hed1)
                    kx1 = enc[xp1]
                    kx = enc[xp] # kx is the encrypted version of our original two letter word
                    # if 2 common letters assign other letter in TR array
                    # to other letter in two_common array
                    if(zp[0] == kx and zp[1] == kx1):
                        if(zp[2] not in enc):
                            if(TR[holo][2] not in dec):
                                alph_keys[TR[holo][2]].append(zp[2])
                    qa += 1
        if(TR[holo][0] in dec):
            if(TR[holo][2] in dec):
                hed = TR[holo][0]
                hed1 = TR[holo][2]
                ha = [] # holder array
                qa = 0
                # get all the words with that letter at start of them in two_common by loop
                while(qa < len(three_common)):
                    zp = ""
                    zp = three_common[qa]
                    ew = 0
                    xp = dec.index(hed)
                    xp1 = dec.index(hed1)
                    kx1 = enc[xp1]
                    kx = enc[xp] # kx is the encrypted version of our original two letter word
                    # if 2 common letters assign other letter in TR array
                    # to other letter in two_common array
                    if(zp[0] == kx and zp[2] == kx1):
                        if(zp[1] not in enc):
                            if(TR[holo][1] not in dec):
                                alph_keys[TR[holo][1]].append(zp[1])
                    qa += 1
        if(TR[holo][1] in dec):
            if(TR[holo][2] in dec):
                hed = TR[holo][1]
                hed1 = TR[holo][2]
                ha = [] # holder array
                qa = 0
                # get all the words with that letter at start of them in two_common by loop
                while(qa < len(three_common)):
                    zp = ""
                    zp = three_common[qa]
                    ew = 0
                    xp = dec.index(hed)
                    xp1 = dec.index(hed1)
                    kx1 = enc[xp1]
                    kx = enc[xp] # kx is the encrypted version of our original two letter word
                    # if 2 common letters assign other letter in TR array
                    # to other letter in two_common array
                    if(zp[1] == kx and zp[2] == kx1):
                        if(zp[0] not in enc):
                            if(TR[holo][0] not in dec):
                                alph_keys[TR[holo][0]].append(zp[0])
                    qa += 1
        holo += 1




def check_fourgrams():
    # get all three letter words that contain letters you have rn
    col = ""
    FU = []
    holo = 0
    for col in four:
        od = 0
        while(od < len(enc)):
            if(alph_keys[enc[od]][0] in col):
                FU.append(col)
            od += 1
        holo = 0
    while(holo < len(FU)):
        sz = 0
        let = ""
        hed = ""
        hed1 = ""
        hed2 = ""
        # this is only the case for the first letter not the second one
        # this is only the case for the first letter not the second one
        if(FU[holo][0] in dec):
            if(FU[holo][1] in dec):
                if(FU[holo][2] in dec):
                    hed = FU[holo][0]
                    hed1 = FU[holo][1]
                    hed2 = FU[holo][2]
                    ha = [] # holder array
                    qa = 0
                    # get all the words with that letter at start of them in two_common by loop
                    while(qa < len(four_common)):
                        zp = ""
                        zp = four_common[qa]
                        ew = 0
                        xp = dec.index(hed)
                        xp1 = dec.index(hed1)
                        kx1 = enc[xp1]
                        xp2 = dec.index(hed2)
                        kx2 = enc[xp2]
                        kx = enc[xp] # kx is the encrypted version of our original two letter word
                        # if 3 common letters assign other letter in FU array
                        # to other letter in two_common array
                        if(zp[0] == kx and zp[1] == kx1 and zp[2] == kx2):
                            if(zp[3] not in enc):
                                if(FU[holo][3] not in dec):
                                    alph_keys[FU[holo][3]].append(zp[3])
                        qa += 1
        if(FU[holo][0] in dec):
            if(FU[holo][2] in dec):
                if(FU[holo][3] in dec):
                    hed = FU[holo][0]
                    hed1 = FU[holo][2]
                    hed2 = FU[holo][3]
                    ha = [] # holder array
                    qa = 0
                    # get all the words with that letter at start of them in two_common by loop
                    while(qa < len(four_common)):
                        zp = ""
                        zp = four_common[qa]
                        ew = 0
                        xp = dec.index(hed)
                        xp1 = dec.index(hed1)
                        kx1 = enc[xp1]
                        xp2 = dec.index(hed2)
                        kx = enc[xp] # kx is the encrypted version of our original two letter word
                        kx2 = enc[xp2]
                        # if 3 common letters assign other letter in FU array
                        # to other letter in two_common array
                        if(zp[0] == kx and zp[2] == kx1 and zp[3] == kx2):
                            if(zp[1] not in enc):
                                if(FU[holo][1] not in dec):
                                    alph_keys[FU[holo][1]].append(zp[1])
                        qa += 1
        if(FU[holo][1] in dec):
            if(FU[holo][2] in dec):
                if(FU[holo][3] in dec):
                    hed = FU[holo][1]
                    hed1 = FU[holo][2]
                    hed2 = FU[holo][3]
                    ha = [] # holder array
                    qa = 0
                    # get all the words with that letter at start of them in two_common by loop
                    while(qa < len(four_common)):
                        zp = ""
                        zp = four_common[qa]
                        ew = 0
                        xp = dec.index(hed)
                        xp1 = dec.index(hed1)
                        xp2 = dec.index(hed2)
                        kx1 = enc[xp1]
                        kx = enc[xp] # kx is the encrypted version of our original two letter word
                        kx2 = enc[xp2]
                        # if 3 common letters assign other letter in FU array
                        # to other letter in two_common array
                        if(zp[1] == kx and zp[2] == kx1 and zp[3] == kx2):
                            if(zp[0] not in enc):
                                if(FU[holo][0] not in dec):
                                    alph_keys[FU[holo][0]].append(zp[0])
                        qa += 1
        if(FU[holo][0] in dec):
            if(FU[holo][1] in dec):
                if(FU[holo][3] in dec):
                    hed = FU[holo][0]
                    hed1 = FU[holo][1]
                    hed2 = FU[holo][3]
                    ha = [] # holder array
                    qa = 0
                    # get all the words with that letter at start of them in two_common by loop
                    while(qa < len(four_common)):
                        zp = ""
                        zp = four_common[qa]
                        ew = 0
                        xp = dec.index(hed)
                        xp1 = dec.index(hed1)
                        kx1 = enc[xp1]
                        kx = enc[xp] # kx is the encrypted version of our original two letter word
                        xp2 = dec.index(hed2)
                        kx2 = enc[xp2]
                        # if 3 common letters assign other letter in FU array
                        # to other letter in two_common array
                        if(zp[0] == kx and zp[1] == kx1 and zp[3] == kx2):
                            if(zp[2] not in enc):
                                if(FU[holo][2] not in dec):
                                    alph_keys[FU[holo][2]].append(zp[2])
                        qa += 1
        holo += 1

def common_endings():
    # look for ing,ght at the end of a word.
    # loop through all the words array
    # by getting index of last letter
    # then checking last letter = decrypted of g
    # then checking last index - 1 = decrypted of c
    # then checking last index - 2 = decrypted of i

    rer = ""
    decg = {}
    dect = {}
    dech = {}
    # look for ing the end of a word.
    # loop through all the words array
    # by getting index of last letter
    # then checking last letter = decrypted of t
    # then checking last index - 1 = decrypted of h
    # then checking last index - 2 = decrypted of g
    # put all possibilities in a dictionary
    for rer in Awords:
        rer = rer.lower()
        xq = len(rer) - 1
        foe = 0
        while(foe < len(rer)):
            if(foe >= 2):
                if(alph_keys[rer[foe]][0] == "t" and rer[foe] in dec):
                    if(alph_keys[rer[foe - 1] ][0] == "h" and rer[foe - 1] in dec):
                        if(alph_keys[rer[foe - 2]][0] != "g"):
                            if(rer[foe - 2][0] not in dec):
                                if("g" not in enc):
                                    if(rer[foe - 2] not in decg):
                                        decg[rer[foe - 2]] = 1
                                    else:
                                        decg[rer[foe - 2]] += 1
                if(alph_keys[rer[foe]][0] != "t"):
                    if(alph_keys[rer[foe - 1] ][0] == "h" and rer[foe - 1] in dec):
                        if(alph_keys[rer[foe - 2]][0] == "g" and rer[foe - 2] in dec):
                            if("g" not in enc):
                                if(rer[foe] not in dec):
                                    if(rer[foe] not in dect):
                                        dect[rer[foe]] = 1
                                    else:
                                        dect[rer[foe]] += 1
                if(alph_keys[rer[foe]][0] == "t" and rer[foe] in dec):
                    if(alph_keys[rer[foe - 1] ][0] != "h"):
                        if(alph_keys[rer[foe - 2]][0] == "g" and rer[foe - 2] in dec):
                            if("n" not in enc):
                                if(rer[foe - 1] not in dec):
                                    if(rer[foe - 1] not in dech):
                                        dech[rer[foe - 1]] = 1
                                    else:
                                        dech[rer[foe - 1]] += 1
            foe += 1
    # get max possible value of each dictionary
    # and then assign to key dictionary
    beau = ""
    kipo = ""
    enj = ""
    if(len(decg) > 0):
        beau = max(decg, key= decg.get)
        alph_keys_1["g"][0] = beau
        alph_keys[beau][0] = "g"
        enc.append("g")
        dec.append(beau)
    if(len(dech) > 0):
        kipo = max(dech, key= dech.get)
        alph_keys_1["h"][0] = kipo
        alph_keys[kipo][0] = "h"
        enc.append("h")
        dec.append(kipo)
    if(len(dect) > 0):
        enj = max(dect, key= dect.get)
        alph_keys_1["t"][0] = enj
        alph_keys[enj][0] = "t"
        enc.append("t")
        dec.append(enj)
    deci = {}
    decg = {}
    decn = {}
    ger = ""
    # look for ing the end of a word.
    # loop through all the words array
    # by getting index of last letter
    # then checking last letter = decrypted of g
    # then checking last index - 1 = decrypted of n
    # then checking last index - 2 = decrypted of i
    # put all possibilities in a dictionary
    for ger in Awords:
        ger = ger.lower()
        xq = len(ger) - 1
        if(len(ger[xq]) > 2):
            if(alph_keys[ger[xq]][0] == "g" and ger[xq] in dec):
                if(alph_keys[ger[xq - 1] ][0] == "n" and ger[xq - 1] in dec):
                    if(alph_keys[ger[xq - 2]][0] != "i"):
                        if("i" not in enc):
                                if(ger[xq - 2] not in dec):
                                    if(ger[xq - 2] not in deci):
                                        deci[ger[xq - 2]] = 1
                                else:
                                    deci[ger[xq - 2]] += 1
            if(alph_keys[ger[xq]][0] != "g" ):
                if(alph_keys[ger[xq - 1] ][0] == "n" and ger[xq - 1] in dec):
                    if(alph_keys[ger[xq - 2]][0] == "i" and ger[xq - 2] in dec):
                        if("g" not in enc):
                            if(ger[xq] not in dec):
                                if(ger[xq] not in decg):
                                    decg[ger[xq]] = 1
                                else:
                                    decg[ger[xq]] += 1
            if(alph_keys[ger[xq]][0] == "g" and ger[xq] in dec):
                if(alph_keys[ger[xq - 1] ][0] != "n" ):
                    if(alph_keys[ger[xq - 2]][0] == "i" and ger[xq - 2] in dec):
                        if("n" not in enc):
                            if(ger[xq - 1] not in dec):
                                if(ger[xq - 1] not in decn):
                                    decn[ger[xq - 1]] = 1
                                else:
                                    decn[ger[xq - 1]] += 1
    # get all max values of possibilities dictionary
    # then assign to key dictionary
    beau1 = ""
    kipo1 = ""
    enj1 = ""
    if(len(decg) > 0):
        beau1 = max(decg, key= decg.get)
        alph_keys_1["g"][0] = beau1
        alph_keys[beau1][0] = "g"
        enc.append("g")
        dec.append(beau1)
    if(len(deci) > 0):
        kipo1 = max(deci, key= deci.get)
        alph_keys_1["i"][0] = kipo1
        alph_keys[kipo1][0] = "i"
        enc.append("i")
        dec.append(kipo1)
    if(len(decn) > 0):
        enj1 = max(decn, key= decn.get)
        alph_keys_1["n"][0] = enj1
        alph_keys[enj1][0] = "n"
        enc.append("n")
        dec.append(enj1)
    ber = ""
    decn1 = {}
    decc = {}
    dece1 = {}
     # look for nce the end of a word.
    # loop through all the words array
    # by getting index of last letter
    # then checking last letter = decrypted of e
    # then checking last index - 1 = decrypted of c
    # then checking last index - 2 = decrypted of n
    # put all possibilities in a dictionary
    for ber in Awords:
        ber = ber.lower()
        che = 0
        while(che < len(ber) - 2):
            if(alph_keys[ber[che + 2]][0] == "e" and ber[che] in dec):
                if(alph_keys[ber[che + 1] ][0] == "c" and ber[che + 1] in dec):
                    if(alph_keys[ber[che]][0] != "n"):
                        if("n" not in enc):
                            if(ber[che] not in dec):
                                if(ber[che] not in decn1):
                                    decn1[ber[che]] = 1
                                else:
                                    decn1[ber[che]] += 1
            if(alph_keys[ber[che + 2]][0] != "e"):
                if(alph_keys[ber[che + 1] ][0] == "c" and ber[che + 1] in dec):
                    if(alph_keys[ber[che]][0] == "n" and ber[che] in dec):
                        if("e" not in enc):
                            if(ber[che + 2] not in dec):
                                if(ber[che + 2] not in dece1):
                                    dece1[ber[che + 2]] = 1
                                else:   
                                    dece1[ber[che + 2]] += 1
            if(alph_keys[ber[che + 2]][0] == "e" and ber[che + 2] in dec):
                if(alph_keys[ber[che + 1]][0] != "c"):
                    if(alph_keys[ber[che]][0] == "n" and ber[che] in dec):
                        if("c" not in enc):
                            if(ber[che + 1] not in dec):
                                if(ber[che + 1] not in decc):
                                    decc[ber[che + 1]] = 1
                                else:
                                    decc[ber[che + 1]] += 1
            che += 1
    #get all max values from possibilities dictionary
    #then assign values to keys dictionary
    beau2 = ""
    kipo2 = ""
    enj2 = ""
    if(len(decn1) > 0):
        beau2 = max(decn1, key= decn1.get)
        alph_keys_1["n"][0] = beau2
        alph_keys[beau2][0] = "n"
        enc.append("n")
        dec.append(beau2)
    if(len(dece1) > 0):
        kipo2 = max(dece1, key= dece1.get)
        alph_keys_1["e"][0] = kipo2
        alph_keys[kipo2][0] = "e"
        enc.append("e")
        dec.append(kipo2)
    if(len(decc) > 0):
        enj2 = max(decc, key= decc.get)
        alph_keys_1["c"][0] = enj2
        alph_keys[enj2][0] = "c"
        enc.append("c")
        dec.append(enj2)

def check_doubles():
    # look for all the double letters
    # loop through all three,four letter words
    # if two letters of looped word are equal and are next to each other
    # and are equal to letters in common_double words and are at the same index
    # can assign values to keys dictionary
    lum = ""
    for lum in three:
        lum = lum.lower()
        if(lum[1] == lum[2]):
            # now loop through all the common 3letter doubles
            ux = 0
            fe = ""
            while(ux < len(com_3_doubles)):
                fe = com_3_doubles[ux]
                if(com_3_doubles[ux][0] in enc):
                    if(alph_keys[lum[0]][0] == com_3_doubles[ux][0]):
                        if(com_3_doubles[ux][1] not in enc):
                            alph_keys[lum[1]].append(com_3_doubles[ux][1])
                            alph_keys[lum[1]].pop(0)
                            alph_keys_1[com_3_doubles[ux][1]].append(lum[1])
                            alph_keys_1[com_3_doubles[ux][1]].pop(0)
                            enc.append(com_3_doubles[ux][1])
                            dec.append(lum[1])
                ux += 1
    # look for all the double letters
    erg = ""
    for erg in four:
        if(erg[2] == erg[3]):
            # now loop through all the common 4letter doubles
            ux = 0
            fe = ""
            while(ux < len(com_4_doubles)):
                fe = com_4_doubles[ux]
                if(com_4_doubles[ux][1] in enc):
                    if(alph_keys[erg[1]][0] == com_4_doubles[ux][1]):
                        if(com_4_doubles[ux][2] not in enc):
                            alph_keys[erg[2]].append(com_4_doubles[ux][2])
                            alph_keys[erg[2]].pop(0)
                            alph_keys_1[com_4_doubles[ux][2]].append(erg[2])
                            alph_keys_1[com_4_doubles[ux][2]].pop(0)
                            enc.append(com_4_doubles[ux][2])
                            dec.append(erg[2])
                ux += 1


def remaining_lettters_1():
    alph = ["a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
    "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"]
    alph_pr = {"a":8.2, "b":1.5, "c": 2.8, "d" : 4.3, "e":12.7,"f":2.2,"g":2.0
    ,"h":6.1,"i":7.0,"j":0.2,"k":0.8,"l":4.1,"m":2.4,"n":6.7,"o":7.5,"p":1.9,"q":0.1,
    "r":6.0,"s":6.3, "t":9.1,"u":2.8,"v":1.0,"w":2.4,"x":0.2,"y":2.0,"z":0.1    }

    # look for r and s...
    # look for "re" and "ro"
    # look for st and si.
    # add each possibility to dictionary
    decrypr = {}
    decryps = {}
    if("s" not in enc and "r" not in enc):
        yea = ""
        for yea in Awords:
            yea = yea.lower()
            uet = 0
            while(uet < len(yea)):
                if(alph_keys[yea[uet]][0] in enc):
                    if(alph_keys[yea[uet]][0] == "e" and uet >= 1):
                        if(yea[uet - 1] not in dec):
                            if(yea[uet - 1] not in decrypr):
                                decrypr[yea[uet - 1]] = 1
                            else:
                                decrypr[yea[uet - 1 ]] += 1
                if(alph_keys[yea[uet]][0] in enc):
                    if(alph_keys[yea[uet]][0] == "o" and uet < len(yea) - 1):
                        if(yea[uet + 1] not in dec):
                            if(yea[uet + 1] not in decrypr):
                                decrypr[yea[uet + 1]] = 1
                            else:
                                decrypr[yea[uet + 1]] += 1
                if(alph_keys[yea[uet]][0] in enc):
                    if(alph_keys[yea[uet]][0] == "t" and uet >= 1):
                        if(yea[uet - 1] not in dec):
                            if(yea[uet - 1] not in decryps):
                                decryps[yea[uet - 1]] = 1
                            else:
                                decryps[yea[uet - 1 ]] += 1
                if(alph_keys[yea[uet]][0] in enc):
                    if(alph_keys[yea[uet]][0] == "i" and uet < len(yea) - 1):
                        if(yea[uet + 1] not in dec):
                            if(yea[uet + 1] not in decryps):
                                decryps[yea[uet + 1]] = 1
                            else:
                                decryps[yea[uet + 1]] += 1
                uet += 1
    # get max value from each possibiility dictionary and assign.
    wowo = ""
    wowo = max(decrypr, key= decrypr.get)
    ohoh = max(decryps, key= decryps.get)
    alph_keys_1["r"][0] = wowo
    alph_keys[wowo][0] = "r"
    alph_keys_1["s"][0] = ohoh
    alph_keys[ohoh][0] = "s"
    enc.append("s")
    enc.append("r")
    dec.append(ohoh)
    dec.append(wowo)

    # now check if there are any letters that have not been decrypted that are over the frequency of 4.5
    # if there are then assign them
    # get all remaining letters, how frequently they come up and match to our frequency dictionary
    geth = ""
    remLD = {}
    remL = []
    for geth in Awords:
        sp = 0
        geth = geth.lower()
        while(sp < len(geth)):
            if(geth[sp] not in dec):
                if(geth[sp] not in remLD):
                    remLD[geth[sp]] = 1
                else:
                    remLD[geth[sp]] += 1
            sp += 1 
    remL = []
    sq = 0
    while(sq < len(alph)):
        if(alph[sq] not in enc):
            remL.append(alph[sq])
        sq += 1
    ron = 0
    wind = {}
    while(ron < len(remL)):
        wind[remL[ron]] = alph_pr[remL[ron]]
        ron += 1


    for key,value in wind.items():
        if value >= 4.0:
            sidde = ""
            sidde = max(wind, key= wind.get)
            wind[sidde] = 0
            blind = ""
            blind = max(remLD, key=remLD.get)
            remLD[blind] = 0
            alph_keys[blind][0] = sidde
            alph_keys_1[sidde][0] = blind
            enc.append(sidde)
            dec.append(blind)


def look_other_words():
    common_m_words = ["more","them"]
    # by looking for common words with m
    # look for "more","from","come", "men", "man","some", "much"
    # if have every letter bar m can assume that letter is m
    # assign to possibility dictionary
    god = ""
    dem = {}
    #loop through all words
    for god in Awords:
        god = god.lower()
        ar = 0
        if(len(god) == 4):
            if(alph_keys[god[1]][0] == "o"):
                if(alph_keys[god[2]][0] == "r"):
                    if(alph_keys[god[3]][0] == "e"):
                        if(god[0] not in dec):
                            if(god[0] not in dem):
                                dem[god[0]] = 1
                            else:
                                dem[god[0]] += 1
        if(len(god) == 4):
            if(alph_keys[god[0]][0] == "f"):
                if(alph_keys[god[1]][0] == "r"):
                    if(alph_keys[god[2]][0] == "o"):
                        if(god[3] not in dec):
                            if(god[3] not in dem):
                                dem[god[3]] = 1
                            else:
                                dem[god[3]] += 1
        if(len(god) == 4):
            if(alph_keys[god[0]][0] == "c"):
                if(alph_keys[god[1]][0] == "o"):
                    if(alph_keys[god[3]][0] == "e"):
                        if(god[2] not in dec):
                            if(god[2] not in dem):
                                dem[god[2]] = 1
                            else:
                                dem[god[2]] += 1
        if(len(god) == 3):
            if(alph_keys[god[1]][0] == "e"):
                if(alph_keys[god[2]][0] == "n"):
                    if(god[0] not in dec):
                        if(god[0] not in dem):
                            dem[god[0]] = 1
                        else:
                            dem[god[0]] += 1  
        if(len(god) == 3):
            if(alph_keys[god[1]][0] == "a"):
                if(alph_keys[god[2]][0] == "n"):
                    if(god[0] not in dec):
                        if(god[0] not in dem):
                            dem[god[0]] = 1
                        else:
                            dem[god[0]] += 1
        if(len(god) == 4):
            if(alph_keys[god[0]][0] == "s"):
                if(alph_keys[god[1]][0] == "o"):
                    if(alph_keys[god[3]][0] == "e"):
                        if(god[2] not in dec):
                            if(god[2] not in dem):
                                dem[god[2]] = 1
                            else:
                                dem[god[2]] += 1
        if(len(god) == 4):
            if(alph_keys[god[1]][0] == "u"):
                if(alph_keys[god[2]][0] == "c"):
                    if(alph_keys[god[3]][0] == "h"):
                        if(god[0] not in dec):
                            if(god[0] not in dem):
                                dem[god[0]] = 1
                            else:
                                dem[god[0]] += 1 
    # get max value of possibility dictionary
    if(len(dem) >= 1):
        deno = ""
        deno = max(dem, key= dem.get)
        alph_keys[deno][0] = "m"
        alph_keys_1["m"][0] = deno
        enc.append("m")
        dec.append(deno)

    #find b:
    pav = [] # holder array
    fib = {}
    bx = ""
    # look for bl, ba, bu and be.
    # loop through all words, if has letter and you haven't decrypted letter before
    # add to possibility dictionary
    if("b" not in enc):
        for bx in Awords:
            if(bx not in one):
                bx = bx.lower()
                if(bx[0] not in dec):
                    if(alph_keys[bx[1]][0] == "e"):
                        if(bx[0] not in fib):
                            fib[bx[0]] = 1
                        else:
                            fib[bx[0]] += 2
                    if(alph_keys[bx[1]][0] == "l"):
                        if(bx[0] not in fib):
                            fib[bx[0]] = 1
                        else:
                            fib[bx[0]] += 1
                    if(alph_keys[bx[1]][0] == "a"):
                        if(bx[0] not in fib):
                            fib[bx[0]] = 1
                        else:
                            fib[bx[0]] += 1
                    if(alph_keys[bx[1]][0] == "u"):
                        if(bx[0] not in fib):
                            fib[bx[0]] = 1
                        else:
                            fib[bx[0]] += 1
    # get max value of possibility dictionary
    darl = ""
    if(len(fib) > 0):
        darl = max(fib, key = fib.get)
        alph_keys[darl][0] = "b"
        alph_keys_1["b"][0] = darl
        enc.append("b")
        dec.append(darl)
    # find f
    # look for of, if and add to possibility dictionary
    els = ""
    eyes = {}
    if("f" not in enc):
        for els in two:
            els = els.lower()
            if(alph_keys[els[0]][0] == "o"):
                if(els[1] not in dec):
                    if(els[1] not in eyes):
                        eyes[els[1]] = 1
                    else:
                        eyes[els[1]] += 1
            if(alph_keys[els[0]][0] == "i"):
                if(els[1] not in dec):
                    if(els[1] not in eyes):
                        eyes[els[1]] = 1
                    else:
                        eyes[els[1]] += 1
    # get max value of possibility dictionary
    if(len(eyes) > 0):
        wuty = ""
        wuty = max(eyes, key = eyes.get)
        alph_keys[wuty][0] = "f"
        alph_keys_1["f"][0] = wuty
        enc.append("f")
        dec.append(wuty)                



    decu = {}
    # find u now
    dre = ""
    # find by searching for us,ur,ut,un
    # and also by searching for ou
    if("u" not in enc):
        for dre in Awords:
            dre = dre.lower()
            lp = 0
            while(lp < len(dre)):
                if(lp > 0):
                    if(alph_keys[dre[lp]][0] == "s"):
                        if(dre[lp - 1] not in dec):
                            if(dre[lp - 1] not in decu):
                                decu[dre[lp - 1]] = 1
                            else:
                                decu[dre[lp - 1]] += 1
                    if(alph_keys[dre[lp]][0] == "r"):
                        if(dre[lp - 1] not in dec):
                            if(dre[lp - 1] not in decu):
                                decu[dre[lp - 1]] = 1
                            else:
                                decu[dre[lp - 1]] += 1
                    if(alph_keys[dre[lp]][0] == "t"):
                        if(dre[lp - 1] not in dec):
                            if(dre[lp - 1] not in decu):
                                decu[dre[lp - 1]] = 1
                            else:
                                decu[dre[lp - 1]] += 1
                    if(alph_keys[dre[lp]][0] == "n"):
                        if(dre[lp - 1] not in dec):
                            if(dre[lp - 1] not in decu):
                                decu[dre[lp - 1]] = 1
                            else:
                                decu[dre[lp - 1]] += 1
                    if(alph_keys[dre[lp]][0] == "o"):
                        if(lp < len(dre) - 1):
                            if(dre[lp + 1] not in dec):
                                if(dre[lp + 1] not in decu):
                                    decu[dre[lp + 1]] = 1
                                else:
                                    decu[dre[lp + 1]] += 1
                lp += 1 
    #get max value of possibility dictionary
    if(len(decu) > 0):
        where = ""
        where = max(decu, key = decu.get)
        alph_keys[where][0] = "u"
        alph_keys_1["u"][0] = where
        enc.append("u")
        dec.append(where)

                    


    #find q:
    # look for letters before u
    sun = ""
    alr = {} # holder array
    if("q" not in enc):
        for sun in Awords:
            sun = sun.lower()
            if(len(sun) >= 3):
                if("u" in enc):
                    ret = 0
                    while(ret < len(sun)):
                        if(alph_keys[sun[ret]][0] == "u"):
                            if(sun[ret - 1] not in dec):
                                if(sun[ret - 1] not in alr):
                                    alr[sun[ret - 1]] = 1
                                else:
                                    alr[sun[ret - 1]] += 1
                        ret += 1
    mess = ""
    # now check every letter you just found
    # loop through every word
    # if at any instance a u doesnt come after remove it from dict.
    # q will only occur before u.
    glg = 0
    for key,value in alr.items():
        mind = ""
        for mind in Awords:
            mind = mind.lower()
            lui = 0
            while(lui < len(mind)):
                if(mind[lui] == key and lui != len(mind) - 1):
                    if(mind[lui + 1] != alph_keys_1["u"][0]):
                        alr[mind[lui]] = 0
                lui += 1
    if(len(alr) != 0):
        mess = max(alr, key = alr.get)
        alph_keys[mess][0] = "q"    
        alph_keys_1["q"][0] = mess
        enc.append("q")
        dec.append(mess)

    
    # get remaining letters by probability
    # loop through all the words
    # get all letters which are not yet decrypted
    # then get the amount for each letter
    

    # find y now....
    # found by getting the last letter of every word that has not been decrypted, add to possibility dictionary
    gem = ""
    suff = {}
    for gem in Awords:
        xd = len(gem) - 1
        if(gem[xd] not in dec):
            if(gem[xd] not in suff):
                suff[gem[xd]] = 1
            else:
                suff[gem[xd]] += 1
    # get max value of possibility dictionary
    if(len(suff) != 0):
        mess = max(suff, key = suff.get)
        alph_keys[mess][0] = "y"    
        alph_keys_1["y"][0] = mess
        enc.append("y")
        dec.append(mess)

    #find w:
    pav = [] # holder array
    fih = {}
    bx = ""
    # look for wh, wi and ow.
    if("w" not in enc):
        for bx in Awords:
            if(bx not in one):
                bx = bx.lower()
                if(bx[0] not in dec):
                    if(alph_keys[bx[1]][0] == "h"):
                        if(bx[0] not in fih):
                            fih[bx[0]] = 1
                        else:
                            fih[bx[0]] += 1
                    if(alph_keys[bx[1]][0] == "i"):
                        if(bx[0] not in fih):
                            fih[bx[0]] = 1
                        else:
                            fih[bx[0]] += 1
                    if(alph_keys[bx[0]][0] == "o"):
                        if(bx[1] not in fih):
                            fih[bx[1]] = 1
                        else:
                            fih[bx[1]] += 1
    if(len(fih) != 0):
        mess = max(fih, key = fih.get)
        alph_keys[mess][0] = "w"    
        alph_keys_1["w"][0] = mess
        enc.append("w")
        dec.append(mess)

def remaining_letters():
    # now check if there are any letters that have not been decrypted that are over the frequency of 4.5
    # if there are then assign them
    # get all remaining letters, how frequently they come up and match to our frequency dictionary
    geth = ""
    remLD = {}
    remL = []
    for geth in Awords:
        geth = geth.lower()
        sp = 0
        while(sp < len(geth)):
            if(geth[sp] not in dec):
                if(geth[sp] not in remLD):
                    remLD[geth[sp]] = 1
                else:
                    remLD[geth[sp]] += 1
            sp += 1

    alph = ["a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
    "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"]
    alph_pr = {"a":8.2, "b":1.5, "c": 2.8, "d" : 4.3, "e":12.7,"f":2.2,"g":2.0
    ,"h":6.1,"i":7.0,"j":0.2,"k":0.8,"l":4.1,"m":2.4,"n":6.7,"o":7.5,"p":1.9,"q":0.1,
    "r":6.0,"s":6.3, "t":9.1,"u":2.8,"v":1.0,"w":2.4,"x":0.2,"y":2.0,"z":0.1    }
    sq = 0
    while(sq < len(alph)):
        if(alph[sq] not in enc):
            remL.append(alph[sq])
        sq += 1
    ron = 0
    wind = {}
    while(ron < len(remL)):
        wind[remL[ron]] = alph_pr[remL[ron]]
        ron += 1
    for key,value in wind.items():
        sidde = ""
        sidde = max(wind, key= wind.get)
        wind[sidde] = 0
        blind = ""
        blind = max(remLD, key=remLD.get)
        remLD[blind] = 0
        sidde = sidde.lower()
        blind = blind.lower()
        alph_keys[blind][0] = sidde
        alph_keys_1[sidde][0] = blind
        enc.append(sidde)
        dec.append(blind)


    sq = 0
    bet = ""
    remaL = []
    lettersy = 0
    for bet in Awords:
        bet = bet.lower()
        sq = 0
        while(sq < len(bet)):
            if(bet[sq] not in remaL):
                lettersy += 1
                remaL.append(bet[sq])
            sq += 1




def decipher_text():
    # now decipher the text:
    mot = 0
    alph = ["a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
    "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"]

    #get all the letters which are not in the text
    scl = 0
    feq = []
    while(scl < len(alph)):
        if(alph[scl] not in dec):
            feq.append(alph[scl])
        scl += 1


    # first remove all arrays from alph_keys_1
    while(mot < len(alph)):
        alph_keys_1[alph[mot]] = alph_keys_1[alph[mot]][0]
        mot += 1

    # get every letter from text we are decrypting
    # this will be the value of the dictionary
    # can then add key to string based on the value from each letter
    #alph_keys_1[alph_keys_1.values().index("w")] = "w"

    ic = 0
    chan = ""
    ac_string = ""
    while(ic < len(s)):
        chan = s[ic].lower()
        if(chan.isalpha()):
            if(chan in alph_keys_1.values()):
                dream = alph_keys_1.keys()[alph_keys_1.values().index(chan)]
                if(dream in enc):
                    ac_string += dream
        if(not chan.isalpha()):
            ac_string += chan
        ic += 1
    print("The decrypted text is :")
    print(" ")
    print(ac_string)
    print(" ")
    print("They key is :")
    print(alph_keys_1)
    print("Time it took to execute", round(time.time() - original_time,5))
    #output to file
    fs = open(file_name + "-key.txt", "a")
    i = 0
    for key,value in alph_keys_1.items():
        fs.write( key + " : " + value ) 
        fs.write("\n")
    fs.close()
    re = open(file_name + "-decrypted.txt", "w+")
    re.write(ac_string)
    re.close()

def main():
    # make all the threads:
    procs = []
    threads = []
    procs1 = []
    threads1 = []
    t = threading.Thread(target= first_function())
    threads.append(t)
    t1 = threading.Thread(target= check_bigrams())
    threads.append(t1)
    t2 = threading.Thread(target= check_options())
    threads.append(t2)
    t3 = threading.Thread(target= findand())
    threads.append(t3)
    t4 = threading.Thread(target= check_trigrams())
    threads.append(t4)
    t5 = threading.Thread(target= check_fourgrams())
    threads.append(t5)
    t6 = threading.Thread(target= check_options())
    threads.append(t6)
    t7 = threading.Thread(target= common_endings())
    threads.append(t7)
    t8 = threading.Thread(target= check_doubles())
    threads.append(t8)
    t9 = threading.Thread(target= remaining_lettters_1())
    threads.append(t9)
    l1 = threading.Thread(target= look_other_words())
    threads.append(l1)
    l2 = threading.Thread(target= remaining_letters())
    threads.append(l2)
    l3 = threading.Thread(target= check_options())
    threads.append(l3)
    l4 = threading.Thread(target= decipher_text())
    threads.append(l4)
    uip = 0
    while(uip < len(threads)):
        threads[uip].start()
        threads[uip].join()
        uip += 1


if __name__ == "__main__":
    main()


