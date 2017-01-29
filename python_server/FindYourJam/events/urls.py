from django.conf.urls import url

from . import views

urlpatterns = [
    # params: title, description, lat, long, date, location
    # returns: event_id
    url(r'^create_event', views.create_event),
    # params: event_id
    # returns: title, description, date, location, lat, long
    url(r'^get_event', views.get_event),
    # params: lat, long, radius
    # returns: array: event_id, lat, long
    url(r'^get_multiple_events', views.get_multiple_events),
    # params: event_id
    # returns: none
    url(r'^attend_event', views.attend_event),
    # params: none
    # returns: array: title, description, event_id
    url(r'^get_user_events', views.get_user_events),
    # params: none
    # returns: array: title, description, event_id
    url(r'^get_attended_events', views.get_attended_events),

]