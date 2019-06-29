<template>
    <v-app>
        <v-container>
            <v-switch v-model="show" :label="`Anzeigen: ${show.toString()}`"></v-switch>
            <v-switch
                    v-for="(val,key) in fdata"
                    v-model="fshow[key]" :label="key"/>
            <v-btn @click="get_data" :disabled="loading">
                get_line
            </v-btn>
            <v-btn @click="get_coords" :disabled="loading">
                get_coords
            </v-btn>
            <v-text-field
                    v-model="address"
                    label="Enter the destination address here"
            ></v-text-field>

            <v-layout row>
                <v-flex>
                    <v-text-field
                            v-model="origin"
                            label="Origin"
                    ></v-text-field>
                </v-flex>
                <v-flex>
                    <v-text-field
                            v-model="dest"
                            label="Destination"
                    ></v-text-field>
                </v-flex>
                <v-flex>
                    <v-select
                            value="Transport"
                            v-model="trasport"
                            :items="['bike','foot','car']"
                            menu-props="auto, overflowY"

                    ></v-select>
                </v-flex>
            </v-layout>


            <no-ssr>
                <l-map class="mini-map" :zoom="12" :center="center">
                    <l-tile-layer
                            url="https://api.tiles.mapbox.com/v4/mapbox.streets/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw"
                    ></l-tile-layer>

                    <div v-if="false" v-for="(loc_types,idx) in locs" :key="idx">
                        <l-marker v-for="user in (loc_types)" :lat-lng="[user.lat_lon[0],user.lat_lon[1]]">
                            <l-icon :icon-anchor=" [50/2, 0]">
                                <v-badge overlap bottom color="transparent" style="background-color: transparent;">
                                    <template
                                            v-slot:badge
                                            v-if="(user.id===1&&idx==='users')||user.hasOwnProperty('act')"
                                    >
                                        <v-icon large v-if="user.id===1" color="orange">star</v-icon>
                                        <v-icon large v-if="user.hasOwnProperty('act')" color="#C72961">remove_red_eye
                                        </v-icon>
                                    </template>
                                    <v-card style="height:50px;  background-color: white">
                                        <v-img height="50" width="50" :src="user.image"
                                               style="text-align: right"></v-img>
                                    </v-card>
                                </v-badge>
                            </l-icon>
                            <l-popup>
                                <v-layout column style="text-align: center">
                                    <v-card>
                                        <img v-lazy="user.image" width="100">
                                    </v-card>
                                    <v-flex pt-1>
                                        {{user.address}}
                                        <br>
                                        {{user.name}}
                                    </v-flex>
                                    <!--Description of the article-->
                                    <!--Description of the article-->
                                    <v-card
                                            v-if="user.hasOwnProperty('act')"
                                            :to="'/app/question?act='+user.act.id"
                                            row
                                            align-center
                                            justify-start
                                            fill-height
                                            wight="100%"
                                    >
                                        <v-layout row pa-1>
                                            <v-flex pa-1 xs3>
                                                <v-sheet>
                                                    <v-img height="30px" contain :src="user.act.image"/>
                                                </v-sheet>
                                            </v-flex>
                                            <v-flex pl-2>
                                                <span style="color:#1B777D;font-style: italic">{{user.act.activity}}</span>
                                                :
                                                {{user.act.text}}
                                            </v-flex>
                                        </v-layout>
                                        <v-layout align-right justify-end row pa-1 style="color:#C72961; size:10px">
                                            <v-flex xs2>
                                                <v-icon small color="#C72961">save_alt</v-icon>
                                                2
                                            </v-flex>
                                            <v-flex xs2>
                                                <v-icon small color="#C72961">remove_red_eye</v-icon>
                                                23
                                            </v-flex>
                                            <v-flex xs2>
                                                <v-icon small color="#C72961">comment</v-icon>
                                                3
                                            </v-flex>
                                        </v-layout>
                                    </v-card>
                                </v-layout>
                            </l-popup>
                        </l-marker>
                    </div>
                    <div v-if="show">
                        <l-geo-json
                                :geojson="stadteile"
                                :options="options"
                                :options-style="styleFunction"
                                v-for="(val1, key) in stadteile" :key="key"
                        >

                        </l-geo-json>

                    </div>
                    <l-geo-json
                            :geojson="geojsons"
                            :options-style="styleFunction"

                    >

                    </l-geo-json>
                    <l-geo-json v-if="show"
                                :options="options"
                                :geojson="mundraub"
                                :options-style="styleFunction"

                    >

                    </l-geo-json>
                    <!--MARKERS FOR FILTERS-->
                    <div v-for="(filter, fkey) in fdata" :key="fkey"
                         v-if="fshow[fkey]"
                    >
                        <l-marker
                                v-for="(val,key) in filter.features"
                                :lat-lng="[
                                val.geometry.coordinates[0],
                                val.geometry.coordinates[1]
                                ]"
                        >
                            <l-icon :icon-anchor=" [30/2, 0]">
                                <v-img height="30" width="30"
                                       contain
                                       src="https://mundraub.org/modules/custom/mundraub_map/img/campaigns.svg"/>
                            </l-icon>
                            <l-popup>
                                <v-layout column style="text-align: center">

                                    <v-flex v-for="(val1,key1) in val.properties">
                                        {{key1}}:{{val1}}
                                    </v-flex>
                                </v-layout>

                            </l-popup>

                        </l-marker>
                    </div>
                    <!--END MARKERS FOR FILTERS-->
                </l-map>
            </no-ssr>
        </v-container>
    </v-app>
</template>

<script>
  import axios from "axios";
  import Vue from "vue";
  import {users, banks, courses, acts} from "./jsons.js";
  import {stadteile} from "./Stadtbezirke_EPSG4326_JSON.js";
  import {mundraub} from "./mundraub.js";

  var app_key = "e341f9a9-da78-4cfd-bb37" + "-" + "3c682e921182";
  // import {icon} from "leaflet";
  axios.defaults.headers.get = {
    // "Access-Control-Allow-Origin": "*",
    // "Access-Control-Allow-Credentials": "true",
    "Content-Type": "application/json"
  };
  axios.defaults.method = "get";

  export default {

    data: () => ({

      // Travel time describes the stage on which it is traveling
      users: users,
      fdata: {
        mundraub: mundraub,
      },
      fshow: {
        mundraub:false,
      },
      address: null,
      banks: banks,
      center: [51.2917076298, 7.2510191991],
      origin: "51.217469, 6.804767",
      dest: "51.2191094, 6.8043112",
      acts: acts,
      show: false,
      geojson: null,
      stadteile: null,
      locs: {
        banks: banks,
        users: users,
        courses: courses
      },
      travel_time: 0,
      sex: true,
      sources: {
        true: "scout",
        false: "airbnb"
      },
      toggle_exclusive: 0,
      long_stay: true,
      max_price: {
        true: 1500,
        false: 200
      },
      age: 35,
      markers: [[], [], []],
      style: {
        color: "#000",
        weight: 1,
        opacity: 0.3,
        fillOpacity: 0.3
      },
      trasport: 'car',
      loading: false,
      geojsons: [{type: "Point", coordinates: [7.2510191991, 51.2917076298]}]
    }),
    methods: {
      //Calculating route with Graphhopper
      get_data: function () {
        this.loading = true;
        this.geojsons = [];

        console.log("Starting Axsios", this.api_host);
        var url_1 = "https://graphhopper.com/api/1//route?point=" + this.origin + "&point=" + this.dest + "&type=geojson&locale=de-DE&vehicle=" + this.trasport + "&weighting=fastest&elevation=true&key=" + app_key + "&instructions=false&points_encoded=false";
        axios
            .get(url_1)
            .then(response => {
              console.log("Loading");
              this.geojsons = response.data.paths[0].points;
              console.log(this.geojsons);
              this.loading = false;
              this.center = [this.geojsons.coordinates[0][1], this.geojsons.coordinates[0][0]];
            })
            .catch(error => {
              // Handle Errors here.
              this.loading = false;

              var errorCode = error.code;
              var errorMessage = error.message;
              console.log(errorCode, errorMessage);
              // ...
            });
      },
      //This get work with HERE API
      get_coords: function () {
        this.loading = true;
        console.log("Starting Axsios get_coords");
        let url_1 = "https://geocoder.api.here.com/6.2/geocode.json?searchtext=" + this.address + "&app_id=jmkpDC63yWSbK3" + "644Zdi&app_code=lBPRtQcZIyXg" + "WurwgzCUkw&gen=8";
        axios
            .get(url_1)
            .then(response => {
              console.log("Loading");
              let dest_temp = response.data.Response.View[0].Result[0].Location.NavigationPosition[0];
              this.dest = dest_temp.Latitude + "," + dest_temp.Longitude;
              console.log("Dest", this.dest);
              this.loading = false;
            })
            .catch(error => {
              // Handle Errors here.
              this.loading = false;

              var errorCode = error.code;
              var errorMessage = error.message;
              console.log(errorCode, errorMessage);
              // ...
            });
      }
    },
    watch: {},
    created() {
      for (let i = 0; i < acts.length; i++) {
        let user = acts[i].user;
        this.users[user].act = acts[i];
        this.users[user].act.id = i;
      }
      ;
      this.stadteile = stadteile[0].features;
      console.log(this.stadteile);

    },
    computed: {
      api_host() {
        console.log("Get api_host");
        var url_1 = "https://graphhopper.com/api/1//route?point=" + this.origin + "&point=" + this.dest + "&type=geojson&locale=de-DE&vehicle=foot&weighting=fastest&elevation=true&key=" + app_key + "&instructions=false&points_encoded=false";
        return url_1
      },
      options() {
        return {
          onEachFeature: this.onEachFeatureFunction
        };
      },
      styleFunction() {
        return () => {
          return {
            weight: 2,
            color: "#9ca8f1",
            opacity: 1,
            fillColor: "#7b83e4",
            fillOpacity: 0.02
          };
        };
      },
      onEachFeatureFunction() {
        return (feature, layer) => {
          let tool_tip = "";
          for (let key in feature.properties) {
            tool_tip = tool_tip + "<div>" + key + ":" + feature.properties[key] + " </div>"
          }
          layer.bindTooltip(
              tool_tip,
              {permanent: false, sticky: true}
          );
        };
      },
    },
  }
  ;
</script>

<style scoped>
    .mini-map {
        width: 100%;
        height: 50vh !important;
    }
</style>
