const fs=require('fs');

let plants=JSON.parse(fs.readFileSync('plant.json', 'utf-8'));
let fruits=[];
for(let fruit of plants.features) {
	fruits.push({
		"type": "Feature",
		"geometry": {
			"type": "Point",
			"coordinates": fruit.pos,
		},
		"properties": {
			"node_id": fruit.properties.nid,
			"type_id": fruit.properties.tid,
			"icon": fruit.properties.icon,
			"type": "plant",
			"title": fruit.properties.fruit
		}
	});
}
let cideries=JSON.parse(fs.readFileSync('cidery.json', 'utf-8'));
for(cidery of cideries.features) {
	fruits.push({
		"type": "Feature",
		"geometry": {
			"type": "Point",
			"coordinates": cidery.pos,
		},
		"properties": {
			"node_id": cidery.properties.nid,
			"icon": "cidery",
			"type": "cidery",
			"title": "Cidery"
		}
	});

}
fs.writeFileSync('mundraub.json',JSON.stringify(fruits));
