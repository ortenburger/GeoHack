from flask import Blueprint
from flask_restplus import Api

from .sources import sources as ns_sources

api_bp = Blueprint('api_bp', __name__, url_prefix='/api/v1')
api = Api(api_bp, description='Wuppertal Data API')

api.add_namespace(ns_sources)
