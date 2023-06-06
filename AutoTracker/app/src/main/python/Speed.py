import requests
import json

import pandas as pd
from sklearn.tree import DecisionTreeClassifier

from os.path import dirname, join


def get_speedlimit(lat,long):
    latitude = str(lat)
    longitude = str(long)
    r = requests.get('http://dev.virtualearth.net/REST/v1/Routes/SnapToRoad?points=' + latitude +',' + longitude + '&interpolate=false&includeSpeedLimit=true&includeTruckSpeedLimit=false&speedUnit=KPH&travelMode=driving&key=As8vM1g3KvKYfy7gVutkLX7Uv7IU9G34VLKwO7xdXwgNbcHD0jlNea3U_KLCr_7l')
    data = r.json()
    try:
        return data["resourceSets"][0]["resources"][0]["snappedPoints"][0]["speedLimit"]
    except IndexError as e:
        return 0

def get_driving_style(array,arraylength,pre_slow,pre_norm,pre_aggr,pre_total):
    print("python")
    i = 0
    array2 = []
    while(i < arraylength):
        array2.append(array.get(i))
        i += 1
    filename = join(dirname(__file__), "test_motion_data.csv")
    driving_beh = pd.read_csv(filename)
    driving_beh.drop('Timestamp', axis=1, inplace=True)
    x = driving_beh.drop(columns=['Class'])
    y = driving_beh['Class']
    model = DecisionTreeClassifier()
    model.fit(x,y)
    modelarray = model.predict(array2)
    modelarray = modelarray.tolist()
    #remove first element as both sensors havent been accounted for
    if(len(modelarray) > 0):
        modelarray.pop(0)
    perc_array = []
    print(len(modelarray))
    perc_array.append(int((modelarray.count("SLOW") + pre_slow) / (len(modelarray) + pre_total) * 100))
    perc_array.append(int((modelarray.count("NORMAL") + pre_norm) / (len(modelarray) + pre_total) * 100))
    perc_array.append(int((modelarray.count("AGGRESSIVE") + pre_aggr) / (len(modelarray) + pre_total) * 100))
    perc_array.append(modelarray.count("SLOW") + pre_slow)
    perc_array.append(modelarray.count("NORMAL") + pre_norm)
    perc_array.append(modelarray.count("AGGRESSIVE") + pre_aggr)
    perc_array.append(len(modelarray) + pre_total)
    return perc_array
def read_file():
    filename = join(dirname(__file__), "Text.rtf")

    with open(filename, 'r' , encoding='utf8' , errors = "ignore") as fin:
        data = fin.read().lower()
    
    return data
    #df = pd.read_csv(filename)


