# -*- coding: utf-8 -*-
# Generated by Django 1.10.5 on 2017-01-29 05:37
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('events', '0004_event_location'),
    ]

    operations = [
        migrations.AlterField(
            model_name='event',
            name='lat',
            field=models.FloatField(default=0),
        ),
        migrations.AlterField(
            model_name='event',
            name='long',
            field=models.FloatField(default=0),
        ),
    ]
