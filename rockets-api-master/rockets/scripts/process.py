#!/usr/bin/env python
# -*- coding: utf-8 -*-

import geopandas as gpd
from pathlib import Path

PWD = Path(__file__).parent.parent
ORIGINAL = PWD / 'archive' / 'solar.geojson'
OUTDIR = PWD / 'data'

# Open the "original" data file
print(f'Reading original data from {ORIGINAL}...')
df = gpd.read_file(ORIGINAL)

# Dropping irrelevant fields from the KML export
df = df.drop(['description', 'id', 'name'], axis=1)

# Produce the small, medium and large files
print(f'Writing out data to {OUTDIR}')
df[:1000].to_file(OUTDIR / 'small.geojson', driver='GeoJSON')
df[:3000].to_file(OUTDIR / 'medium-small.geojson', driver='GeoJSON')
df[:10000].to_file(OUTDIR / 'lg-medium-small.geojson', driver='GeoJSON')
