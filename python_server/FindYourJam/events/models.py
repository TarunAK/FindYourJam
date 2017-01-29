from __future__ import unicode_literals
import uuid
from django.db import models
from datetime import datetime

# Create your models here.
from django.contrib.auth.models import User


class Event(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    title = models.CharField(max_length=30, blank=False)
    description = models.CharField(max_length=140)
    date = models.DateTimeField(blank=True, default=datetime.now)
    date_created = models.DateTimeField(auto_now_add=True, blank=True)
    location = models.CharField(max_length=50, default="Toronto")
    lat = models.IntegerField(blank=False,default=0)
    long = models.IntegerField(blank=False,default=0)
    users_attending = models.ManyToManyField(User, related_name="events_attending")
    user_creator = models.ForeignKey(User, on_delete=models.CASCADE,  related_name="events_created")
