from rest_framework import status
from rest_framework.response import Response
from rest_framework.authentication import TokenAuthentication
from rest_framework.decorators import api_view, authentication_classes, permission_classes
from rest_framework.permissions import IsAuthenticated
from events.models import Event
import json
import uuid


# Create your views here.
@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
@permission_classes((IsAuthenticated,))
def create_event(request):
    """
    Create a user event
    """
    user = request.user
    request_data = json.loads(request.body)

    if not (request_data.get('title') and request_data.get('description') and
            request_data.get('lat') and request_data.get('long') and
            request_data.get('date') and request_data.get('location')):
        response_data = {
            'status': 'error'
        }
        return Response(data=response_data, status=status.HTTP_400_BAD_REQUEST)
    print ('creating event')
    print (request_data.get('lat'))
    print (request_data.get('long'))
    event = Event(title=request_data.get('title'),
                  description=request_data.get('description'),
                  lat=request_data.get('lat'),
                  long=request_data.get('long'),
                  location=request_data.get('location'),
                  date=request_data.get('date'),
                  user_creator=user)
    event.save()
    response_data = {
        'status': 'success',
        'event_id': event.id
    }
    return Response(data=response_data, status=status.HTTP_200_OK)


@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
@permission_classes((IsAuthenticated,))
def get_event(request):
    """
    Get Events in an area
    """
    request_data = json.load(request.body)
    if not (request_data.get('event_id')):
        response_data = {
            'status': 'error'
        }
        return Response(data=response_data, status=status.HTTP_400_BAD_REQUEST)
    event_id = uuid.UUID(request_data.get('event_id')).hex
    event = Event.objects.get(id=event_id)
    response_data = {
        'status': 'success',
        'title': event.title,
        'description': event.description,
        'date': event.date,
        'location': event.location,
        'lat': event.lat,
        'long': event.long
    }
    return Response(data=response_data, status=status.HTTP_200_OK)


@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
@permission_classes((IsAuthenticated,))
def get_multiple_events(request):
    """
    Get Events in an area
    """
    request_data = json.loads(request.body)
    if not (request_data.get('lat') and request_data.get('long') and
            request_data.get('radius')):
        response_data = {
            'status': 'error'
        }
        return Response(data=response_data, status=status.HTTP_400_BAD_REQUEST)
    lat_req = request_data.get('lat')
    long_req = request_data.get('long')
    radius_req = request_data.get('radius')
    event_set = Event.objects.filter(lat__lte=(lat_req+radius_req),
                                     lat__gte=(lat_req-radius_req),
                                     long__lte=(long_req+radius_req),
                                     long__gte=(long_req-radius_req))
    response_array = []
    for event in event_set:
        value = {
            'status': 'success',
            'event_id': event.id,
            'title': event.title,
            'lat': event.lat,
            'long': event.long
        }
        response_array.append(value)
    response_data = {
        'status': 'success',
        'array': response_array
    }
    return Response(data=response_data, status=status.HTTP_200_OK)


@api_view(['GET'])
@authentication_classes((TokenAuthentication,))
@permission_classes((IsAuthenticated,))
def attend_event(request):
    """
    attend an event
    """
    user = request.user
    request_data = json.load(request.body)
    if not (request_data.get('event_id')):
        response_data = {
            'status': 'error'
        }
        return Response(data=response_data, status=status.HTTP_400_BAD_REQUEST)
    event_id = uuid.UUID(request_data.get('event_id')).hex
    event = Event.objects.get(id=event_id)
    event.users_attending.add(user)
    event.save()
    response_data = {
        'status': 'success',
    }
    return Response(data=response_data, status=status.HTTP_200_OK)


@api_view(['GET'])
@authentication_classes((TokenAuthentication,))
@permission_classes((IsAuthenticated,))
def get_user_events(request):
    """
    Get users created events
    """
    user = request.user
    events = user.events_created.all()
    response_array = []
    for event in events:
        data = {
            'title': event.title,
            'description': event.description,
            'event_id': event.id,
        }
        response_array.append(data)
    response_data = {
        'status': 'success',
        'array': response_array
    }
    return Response(data=response_data, status=status.HTTP_200_OK)


@api_view(['GET'])
@authentication_classes((TokenAuthentication,))
@permission_classes((IsAuthenticated,))
def get_attended_events(request):
    """
    Get users created events
    """
    user = request.user
    events = user.events_attending.all()
    response_array = []
    for event in events:
        data = {
            'title': event.title,
            'description': event.description,
            'event_id': event.id,
        }
        response_array.append(data)

    response_data = {
        'status': 'success',
        'array': response_array
    }
    return Response(data=response_data, status=status.HTTP_200_OK)