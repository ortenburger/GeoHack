# Seting up the Enviroment
from marshmallow.utils import get_value
from marshmallow import utils
import json, requests, time, os
from marshmallow import pprint

if __name__ == '__main__':
    from mongoengine import connect
    import os, sys

    ## Seting up the Enviroment
    # Name of the repository. Must be the same cross platforms
    REPOSITORY = 'aindex-alpha'
    HOME = os.path.abspath(__file__).split(REPOSITORY)[0]
    ROOT = os.path.join(HOME, REPOSITORY)
    os.chdir(ROOT)
    print('ROOT:', ROOT)
    sys.path = [os.path.join(ROOT)] + sys.path
    print(json.dumps(sys.path, indent=4))


    ## END Seting up the Enviroment
    class Config:
        SECRET_KEY = 'MVP_SECRET'


    from config import config

    if os.name == 'nt':
        CONFIG_NAME = 'local_dev'
    else:
        # CONFIG_NAME = os.getenv('MVP_CONFIG') or 'default'
        CONFIG_NAME = 'server_dev'
    # if config[CONFIG_NAME].CONFIG_NAME == 'Production':
    mongo_database = connect(config[CONFIG_NAME].MONGOENGINE_DATABASE_NAME,
                             host=config[CONFIG_NAME].MONGOENGINE_DATABASE_HOST,
                             port=config[CONFIG_NAME].MONGOENGINE_PORT,
                             alias='main',
                             serverSelectionTimeoutMS=3000,
                             connect=False,
                             )
    ## Seting up the Enviroment END

## Setting up the enviroment END

# name of the repository. must be the same cross platforms
A = 0
B = 0
import requests
from bs4 import BeautifulSoup
# from mvp.utils.geocoding import get_coordinates


import holidays
from random import SystemRandom
import xmltodict
import math
import time
from json import dumps
from datetime import datetime, timedelta
import geocoder


# for testing
# https://efa.vrr.de/vrr/XSLT_TRIP_REQUEST2?language=de&itdLPxx_transpCompany=vrr
# https://efa.vrr.de/vrr/XSLT_PS_REQUEST2?language=de&itdLPxx_transpCompany=vrr
# 1°=111km, 0.001 = 111 m

# Gets the coordinates from HERE server. Can also use google and many others.
# An external package
def get_coordinates(address):
    api_key = {
        'app_id': "jmkpDC63yWS"+ "bK3644Zdi",
        "app_code": "lBPRtQcZI" + "yXgWurwgzCUkw"
    }
    g = geocoder.here(address, app_id=api_key['app_id'], app_code=api_key['app_code'])
    return g.json


HEADERS = {'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) '
                         'Chrome/39.0.2171.95 Safari/537.36 Opera/49.0'}
VRR_API_URL_ROOT = 'https://openservice-test.vrr.de/static02/'
VRR_COORD_FORMAT = 'WGS84'
XML_TRIP_REQUEST2_DEFAULT = {
    'sessionID': '0',
    'requestID': '0',
    'language': 'DE',
    'coordOutputFormat': 'WGS84',
    'type_origin': 'coord',
    'name_origin': '',
    'type_destination': 'coord',
    'name_destination': '',
    'lineRestriction': 401}  # (alle Linien ausser ICE)


def time_to_int(time_str):
    time_int = time.strptime(time_str, "%H:%M")
    return time_int.tm_hour * 60 + time_int.tm_min


def next_weekday(d, weekday):
    days_ahead = weekday - d.weekday()
    if days_ahead <= 0:  # Target day already happened this week
        days_ahead += 7
    return d + timedelta(days_ahead)


# get the free day
def next_weekday_str(weekday, time_tamplate='%Y:%m:%d:%H:%M:%S'):
    germany_holidays = holidays.Germany()

    d = datetime.now()
    d = d.replace(hour=9, minute=0, second=0)

    day_number = 2
    next_day = next_weekday(d, 2)  # 0 = Monday, 1=Tuesday, 2=Wednesday...
    # This is some how is not working
    while next_day in germany_holidays:
        # print (next_day)
        # print (type(next_day))
        day_number += 7
        next_day = next_weekday(d, day_number)
    day_string = next_day.strftime(time_tamplate)
    return day_string


# Request to VRR
def vrr_get_trip_request(parameters):
    # building the url
    parameters_new = {}
    for par in parameters:
        if parameters[par] != '':
            parameters[par] = str(parameters[par])
            parameters[par] = str.replace(parameters[par], ' ', '+')
            parameters[par] = parameters[par].lower()
            parameters_new[par] = parameters[par]

    # getting the json data
    r = requests.get(VRR_API_URL_ROOT + 'XML_TRIP_REQUEST2' + '?',
                     headers=HEADERS, params=parameters_new)
    r.raise_for_status()
    # print(url.url)
    return r.content


# Formating coordinates
# <x>:<y>:<Koordinaten-format>
# 6787835:51231000:WGS84[dd.ddddd]
def vrr_get_coord_format(coords, offset={'lat': 0, 'lng': 0}):
    coords_new = dict()
    if 'lat' in coords:
        coords_new = coords
    elif 'address' in coords:
        coords_new = get_coordinates(coords['address'])
    elif 'coords' in coords:
        coords = coords['coords']
        if 'lat' in coords:
            coords_new = coords
        else:
            coords_new['lat'] = coords[0]
            coords_new['lng'] = coords[1]
    elif 'coords' in coords:
        coords = coords['coords']
        if 'lat' not in coords:
            coords_new['lat'] = coords[0]
            coords_new['lng'] = coords[1]
    elif len(coords) == 2:
        coords_new['lat'] = coords[0]
        coords_new['lng'] = coords[1]
    else:
        coords_new = get_coordinates(coords['address'])

    y = int((coords_new['lat'] + offset['lat']) * 1000000)
    x = int((coords_new['lng'] + offset['lng']) * 1000000)

    return str(x) + ':' + str(y) + ':' + VRR_COORD_FORMAT


# Identifies equal bus station
def is_equal_bus_station(station1, station2):
    station1_tmp = station1.copy()
    station2_tmp = station2.copy()

    station1_tmp.pop('line')
    station2_tmp.pop('line')

    station1_tmp.pop('coords')
    station2_tmp.pop('coords')

    return station1_tmp == station2_tmp


# Removing Dupplicated Stations
def vrr_remove_station_duplicates(data_all):
    data_all_new = []

    for data in data_all:
        ok_to_append = True
        for idx, data_new in enumerate(data_all_new):
            if is_equal_bus_station(data, data_new):
                ok_to_append = False
                data_all_new[idx]['line'].extend(data['line'])

        if ok_to_append:
            data_all_new.append(data)

    for idx, data_new in enumerate(data_all_new):
        data_all_new[idx]['line'] = remove_list_duplicates(data_all_new[idx]['line'])

    return data_all_new


def remove_list_duplicates(duplicate):
    final_list = []
    for num in duplicate:
        if num not in final_list and len(num) > 0:
            final_list.append(num)
    return final_list


def remove_trip_list_near_duplicates(duplicate):
    final_list = []
    for idx in range(0, len(duplicate) - 1):
        if duplicate[idx]['name'] != duplicate[idx + 1]['name'] and duplicate[idx]['name'] not in duplicate[idx + 1][
            'name'] \
              and len(duplicate[idx]['name']) > 0 or duplicate[idx]['object_type'] == 'route':
            final_list.append(duplicate[idx])
    if len(duplicate) > 0:
        final_list.append(duplicate[-1])
    return final_list


def mean_time(numbers):
    # todo: average for time
    # d = datetime.datetime.strptime(data['travel_time'], '%H:%M')
    # d_new = datetime.datetime.strptime(data_new['travel_time'], '%H:%M')
    # if (d - d_new).total_seconds() < 0:
    #     data_all_new[idx]['travel_time'] = data['travel_time']
    return float(sum(numbers)) / max(len(numbers), 1)


def prepare_departure_time(parameters, departure_time):
    time = next_weekday_str(2).split(':')
    if 'year' in departure_time:
        parameters['itdDateYear'] = departure_time['year']
    else:
        parameters['itdDateYear'] = time[0]
        departure_time['year'] = time[0]

    if 'month' in departure_time:
        parameters['itdDateMonth'] = departure_time['month']
    else:
        parameters['itdDateMonth'] = time[1]
        departure_time['month'] = time[1]

    if 'day' in departure_time:
        parameters['itdDateDay'] = departure_time['day']
    else:
        parameters['itdDateDay'] = time[2]
        departure_time['day'] = time[2]

    if 'hour' in departure_time:
        parameters['itdTimeHour'] = departure_time['hour']
    else:
        parameters['itdTimeHour'] = time[3]
        departure_time['hour'] = time[3]

    if 'minute' in departure_time:
        parameters['itdTimeMinute'] = departure_time['minute']
    else:
        parameters['itdTimeMinute'] = time[4]
        departure_time['minute'] = time[4]
    return parameters, departure_time


## Changes the path to the geojson format of a LineString
def create_geo_path(path_coords_xml):
    # Fomring the coordinates for geo format
    temp_coords = []
    for point in path_coords_xml:
        point_tmp = xmltodict.parse(str(point))
        point_tmp = point_tmp['itdCoordinateBaseElem']

        lat = float(point_tmp['y']) / 1000000
        lng = float(point_tmp['x']) / 1000000
        temp_coords.append([lng, lat])
    geo_line_string = {
        "type": "LineString",
        "coordinates": temp_coords,
    }
    # print ('Geo line string: ',geo_line_string)
    return geo_line_string
    # print('Path',dumps(, indent=4))


def vrr_get_PT_data(itdRouteList, data_all):
    stations_dest_list = []
    # Loop on full routes
    i = 0
    min_duration = 10000
    min_i = 0
    for itdRoute in itdRouteList:
        if time_to_int(itdRoute['publicDuration']) < min_duration:
            # data_raw (dict(itdRoute.attrs))
            min_duration = time_to_int(itdRoute['publicDuration'])
            min_i = i
        i += 1
    start = time.time()
    if True:
        # for itdRoute in  itdRouteList:
        itdRoute = itdRouteList[min_i]
        itdPartialRouteList = itdRoute.find_all('itdPartialRoute')
        # publicDuration - Total time for the trip
        # individualDuration - Time to walk
        # vehicleTime - Time spent in the vehicle
        # publicDuration - individualDuration - vehicleTime = waiting time.
        data_all['travel_time'].append(time_to_int(itdRoute['publicDuration']))
        trip_list = []
        # Loop on partial routes
        for itdPartialRoute in itdPartialRouteList:
            trip_data = itdPartialRoute.find_all('itdMeansOfTransport')[0]

            point_list_tmp = itdPartialRoute.find_all('itdPoint')
            point_list = []
            # Loop on points
            for point in point_list_tmp:
                if 'name=' in str(point):  # and 'x=' in str(point) and 'y=' in str('point')
                    point_list.append(point)

            point_first = point_list[0]
            point_last = point_list[-1]

            # path_coords_xml = itdPartialRoute.find_all('itdPathCoordinates')
            # path_coords_xml = itdPartialRoute.find_all('itdCoordinateBaseElemList')
            path_coords_xml = itdPartialRoute.find_all('itdCoordinateBaseElem')

            geo_line_string = []
            # Craeting the PATH.
            geo_line_string = create_geo_path(path_coords_xml)

            # First Station - Trip - Last Station
            if len(geo_line_string['coordinates']) > 0:
                trip_0 = dict()
                trip_1 = dict()
                trip_2 = dict()

                trip_0['name'] = point_first['name']
                # trip_0['name1'] = point_first['name']
                trip_0['geometry'] = {
                    "type": "Point",
                    "coordinates": geo_line_string['coordinates'][0]  # LNT, LAT
                }
                trip_0['object_type'] = 'station'
                # Selecting type of transportation
                if trip_data['name'] == '' and itdPartialRoute['type'] == 'IT':
                    trip_1['name'] = 'walk'
                else:
                    trip_1['name'] = trip_data['name']
                # trip_1['name2'] = trip
                trip_1['object_type'] = 'route'
                trip_1['type_route'] = itdPartialRoute['type']
                trip_1['type_transport'] = trip_data['type']
                trip_1['timeMinute'] = itdPartialRoute['timeMinute']
                trip_1['geometry'] = geo_line_string

                trip_2['name'] = point_last['name']
                trip_2['object_type'] = 'station'
                # trip_2['name3'] = point_last['name']
                # Deleting double station and extending the line
                trip_2['geometry'] = {
                    "type": "Point",
                    "coordinates": geo_line_string['coordinates'][-1]  # LNT, LAT
                }
                if len(trip_list) > 0 and trip_list[-1]['name'] == trip_0['name']:
                    geo_line_string['coordinates'] = [trip_list[-1]['geometry']['coordinates']] + geo_line_string[
                        'coordinates']

                trip_list.append(trip_0)
                trip_list.append(trip_1)
                trip_list.append(trip_2)
    time_passed = max(time.time() - start, 0.0001)
    print('Time for calculation:', time_passed)
    # trip_list.pop(0)
    # trip_list.pop(-1)
    # pprint(trip_list)
    trip_list = remove_trip_list_near_duplicates(trip_list)
    print('#' * 50)
    # pprint(trip_list)
    data_all['trip'].append(trip_list)
    # data_all['destination']['station'].append(stations_dest_list)
    return data_all


# Main function
def vrr_get_trip_data(parameters_ref):
    main_start = time.time()
    # print ('parameters_ref', parameters_ref)
    parameters = parameters_ref.copy()

    address_origin = parameters.pop('origin')
    address_destination = parameters.pop('destination')
    if 'departure_time' in parameters:
        departure_time = parameters.pop('departure_time')
    else:
        departure_time = dict()

    parameters = {**XML_TRIP_REQUEST2_DEFAULT, **parameters}
    # Parsing the time variable
    parameters, departure_time = prepare_departure_time(parameters, departure_time)

    if 'filter' in parameters:
        filter = parameters.pop('filter')
    else:
        filter = []

    tries = 0
    while tries < 20:
        cryptogen = SystemRandom()
        lat_unit = 111
        lng_unit = 111 * math.cos(2 * math.pi * 51 / 180)

        random_number_1 = cryptogen.uniform(0, tries * 0.1) / lat_unit
        random_number_2 = cryptogen.uniform(0, tries * 0.1) / lng_unit
        random_number_3 = cryptogen.uniform(0, tries * 0.1) / lat_unit
        random_number_4 = cryptogen.uniform(0, tries * 0.1) / lng_unit
        tries += 1

        coords_offset_1 = {'lat': random_number_1, 'lng': random_number_2}
        coords_offset_2 = {'lat': random_number_3, 'lng': random_number_4}

        parameters['name_origin'] = vrr_get_coord_format(address_origin, coords_offset_1)
        parameters['name_destination'] = vrr_get_coord_format(address_destination, coords_offset_2)

        A = 0
        start_time_loop = time.time()
        # Sending the request to VRR
        start = time.time()
        data_raw = vrr_get_trip_request(parameters)
        time_passed = max(time.time() - start, 0.0001)
        print('Time for request from VRR:', time_passed)
        # print('After main Start:', max(time.time() - main_start, 0.0001) )

        A += time.time() - start_time_loop
        # print ('Time vrr_get_trip_request:', '%.2f' % (A))

        # Parsing the resopnse
        start = time.time()
        start_soup = time.time()
        soup = BeautifulSoup(data_raw, 'lxml-xml')
        print('Time for soup:', max(time.time() - start_soup, 0.0001))
        # print('After main Start:', max(time.time() - main_start, 0.0001))

        data_all = dict()
        data_all['travel_time'] = []
        data_all['departure_time'] = departure_time
        data_all['origin'] = dict()
        data_all['destination'] = dict()
        data_all['trip'] = []
        data_all['origin']['station'] = []
        data_all['destination']['station'] = []

        itdRouteList = soup.find_all('itdRoute')
        del soup
        del data_raw

        if len(itdRouteList) > 0:
            # Doing havy parsing of the data.
            data_all = vrr_get_PT_data(itdRouteList, data_all)

            data_all['origin'] = data_all['origin']['station']
            data_all['destination'] = data_all['destination']['station']

            data_all['travel_time'] = min(data_all['travel_time'])  # todo: average
            data_all['trip'] = data_all['trip']
            print('After main Start:', max(time.time() - main_start, 0.0001))
            return data_all

    return []


def frange(x, y, step):
    if step > 0:
        while x <= y:
            yield x
            x += step
    elif step < 0:
        while x >= y:
            yield x
            x += step
    else:
        print('Error step is 0!')


## EXAMPLES and BANCHMERK

def generate_samples():
    import json
    import random
    import math
    import time

    error_no = 0
    ok_no = 0

    # duisburg uni
    lat_start = 51.42783
    lng_start = 6.800974
    radius_start = 50
    angle_start = 0

    N = 100000
    lat_unit = 111
    lng_unit = 111 * math.cos(2 * math.pi * lat_start / 180)

    # print('lat_start=',lat_start,'lng_start=',lng_start,'radius_start=',radius_start,'km')

    lat_1 = lat_start
    lng_1 = lng_start
    previous_time = time.time()
    start_time = time.time()
    for i in range(0, N):
        radius_end = random.uniform(0, radius_start)
        angle_end = random.uniform(0, 2 * math.pi)

        lat_2 = lat_1 + radius_end * math.cos(angle_end) / lat_unit
        lng_2 = lng_1 + radius_end * math.sin(angle_end) / lng_unit

        parameters = dict()
        parameters['origin'] = {'coords': {'lat': lat_1, 'lng': lng_1}}
        parameters['destination'] = {'coords': {'lat': lat_2, 'lng': lng_2}}

        vrr_data = vrr_get_trip_data(parameters)
        # print('parameters=',parameters)
        # print('vrr_data=',vrr_data)
        if len(vrr_data) > 0:
            ok_no += 1
            # print('error= ', error_no, 'ok_no=', ok_no , 'elapsed_time=', round(time.time() - start_time, 2),
            # 'time_delta=', round(time.time() - previous_time, 2),'travel_time=', vrr_data['travel_time'])

            with open('vrr_data_neural.json', 'a', encoding='utf-8') as f:
                result = dict()
                result['parameters'] = parameters
                result['vrr_data'] = vrr_data
                f.write(json.dumps(result, indent=4))
                f.write(",\n")
        else:
            error_no += 1
            # print('error = ', error_no, 'ok_no=', ok_no)
            with open('vrr_error_list.json', 'a', encoding='utf-8') as f:
                result = dict()
                result['parameters'] = parameters
                f.write(json.dumps(parameters, indent=4))
        previous_time = time.time()

    with open('elapsed_time.info') as f:
        elapsed_time = time.time() - start_time
        f.write(elapsed_time)


def simple_test():
    import json
    TEST_FOLDER_NAME = 'tests'
    TEST_FOLDER = os.path.join(os.path.dirname(__file__), TEST_FOLDER_NAME)
    if not os.path.exists(TEST_FOLDER):
        os.makedirs(TEST_FOLDER)
    print('EXAMPLE_FOLDER:', TEST_FOLDER)
    TEST_FILE_NAME = 'vrr_response.json'
    TEST_FILE = os.path.join(TEST_FOLDER, TEST_FILE_NAME)
    print('TEST_FILE', TEST_FILE)
    # parameters = dict()
    # parameters['origin'] = {'address': 'laerholzstraße 80'}
    # parameters['destination'] = {'address': 'ruhr universität bochum'}
    # parameters['departure_time'] = {"year": "2018", "month": "05", "day": "09", "hour": "09", "minute": "00"}

    # parameters = {'origin': {'coords': {'lat': 51.42783, 'lng': 6.800974}}, 'destination': {'coords': {'lat': 51.04720051644023, 'lng': 6.998676598413753}}}
    parameters = {'origin': {'coords': {'lat': 51.42783, 'lng': 6.800974}},
                  'destination': {'coords': {'lat': 51.3909273679304, 'lng': 6.761610520559554}}}

    vrr_data = vrr_get_trip_data(parameters)

    with open(TEST_FILE, 'w', encoding='utf-8') as f:
        json.dump(vrr_data, f, indent=4)
    print(json.dumps(vrr_data))


def plot_map():
    import json
    import random
    import numpy as np
    import os
    import folium

    EXAMPLE_FOLDER_NAME = 'tests'
    EXAMPLE_FOLDER = os.path.join(os.path.dirname(__file__), EXAMPLE_FOLDER_NAME)
    if not os.path.exists(EXAMPLE_FOLDER):
        os.makedirs(EXAMPLE_FOLDER)
    print('EXAMPLE_FOLDER:', EXAMPLE_FOLDER)

    parameters = dict()
    parameters = {'origin': {'coords': {'lat': 51.42783, 'lng': 6.800974}},
                  'destination': {'coords': {'lat': 51.3909273679304, 'lng': 6.761610520559554}}}

    # parameters['origin'] = {'address': 'Duisburg Hbf'}
    # parameters['destination'] = {'address': 'Bochum Hbf'}
    # parameters['destination'] = {'address': 'hansegracht'}

    B = 0
    start_time_loop = time.time()
    vrr_data = vrr_get_trip_data(parameters)
    B += time.time() - start_time_loop
    print('Time for vrr_get_trip_data:', '%.2f' % (B))

    print('Data received:')
    # print(len(json.dumps(vrr_data)))
    # pprint (vrr_data)
    # Saving an example index.html as live demo.
    m = folium.Map(location=[parameters['origin']['coords']['lat'],
                             parameters['origin']['coords']['lng']])

    colors = ['yellow', 'orange', 'red', 'white', 'cyan', 'blue']
    for trip in vrr_data['trip']:
        for trip_object in trip:
            if trip_object['object_type'] == 'route':
                m.add_child(folium.GeoJson(
                    {
                        "type": "Feature",
                        "geometry": trip_object['geometry']
                    },
                    name=trip_object['name']
                    # name=trip_object['name']
                ).add_child(folium.Popup(trip_object['name'] + ' ' + trip_object['timeMinute'])))

    for trip_object in vrr_data['trip'][0]:
        # print (trip_object)
        if trip_object['object_type'] == 'station':
            m.add_child(folium.GeoJson(
                {
                    "type": "Feature",
                    "geometry": trip_object['geometry']
                },
                name=trip_object['name']
            ).add_child(folium.Popup(trip_object['name'])))

    folium.LayerControl().add_to(m)

    m.save(os.path.join(EXAMPLE_FOLDER, 'index.html'), encoding='utf-8')


## End EXAMPLES and BANCHMERK

if "__main__" == __name__:
    # generate_samples()
    # simple_test()

    # while True:
    plot_map()
# print ('')
