import json
import requests
import os
import re
import csv

APPLICATION_URL = os.getenv("APPLICATION_URL","http://ec2-13-59-95-73.us-east-2.compute.amazonaws.com")

def parseToJson(name):
    data = []
    schema_file = os.path.join(os.path.relpath("schema"), "{}.schema.json".format(name))
    csv_file = os.path.join(os.path.relpath("csv"), "{}.csv".format(name))
    with open(schema_file) as fp:
        schema = json.load(fp)
        with open(csv_file) as scv_fp:
            lines = csv.reader(scv_fp)
            for line in lines:
                obj = {}
                values = list(line)
                for key, value in schema.items():
                    index = value["index"]
                    type = value["type"]
                    if len(values) > index:
                        if values[index] is not '':
                            if type == "number":
                                try:
                                    num = re.sub(r"[^(\d|.)]","", values[index])
                                    print "{} = {}".format(values[index],num)
                                    obj[key] = float(num.replace(",","")) if "." in num else int(num.replace(",",""))
                                except ValueError:
                                    obj[key] = 0
                            elif type == "boolean":
                                obj[key] = True if values[index].lower() == "true" else False
                            else:
                                obj[key] = values[index]
                if len(obj.items()) > 0:
                    data.append(obj)
    data_path = os.path.relpath("json")
    if not os.path.exists(data_path):
        os.makedirs(data_path)
    with open(os.path.join(data_path, "{}.json".format(name)),"w") as fp:
        json.dump(data, fp, indent=4, sort_keys=True)
    r = requests.post("{}/api/{}s/update".format(APPLICATION_URL,name), json=data)
    print r.text
    print '{:-<60}OK'.format(name)


def generateSchema(name):
    url = "{}/api/configs/{}.json".format(APPLICATION_URL,name)
    r = requests.get(url)
    data = r.json()
    schema_data = {}
    wd = os.path.relpath("schema")
    out_path = os.path.join(wd, "{}.schema.json".format(name))
    if os.path.exists(out_path) and os.path.isfile(out_path):
        with open(out_path) as fp:
            schema_data = json.load(fp)

    for i, col in enumerate(data["columns"]):
        if not schema_data.has_key(col["name"]):
            print "name = {}, index = {}, type = {}".format(col["name"], i,col["type"])
            schema_data[col["name"]] = dict({"index": i, "type": col["type"] })
        else:
            print "{} col already defined in the file".format(col["name"])

    if not os.path.exists(wd):
        os.makedirs(wd)
    with open(out_path, "w") as fp:
        json.dump(schema_data, fp, indent=4, sort_keys=True)


def genAllSchema():
    generateSchema("customer")
    generateSchema("product")
    generateSchema("sale")


def genAllData():
    parseToJson("customer")
    parseToJson("product")
    parseToJson("sale")

genAllData()
#genAllSchema()