import cv2
import numpy as np
import sys

import socket
from contextlib import closing

# tcp
def send():
    print "send"
    host = '192.168.11.50'
    port = 12000
    bufsize = 4096

    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    with closing(sock):
        sock.connect((host,port))
        sock.send(b"capture")
    return



# OpenCV Test
capture = cv2.VideoCapture(0)

if capture.isOpened() is False:
    raise("IOError")

pastImage = None
image     = None

while True :
    pastImage = image
    ret, image = capture.read()
    image     = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    if ret == False :
        continue

    if image is not None and pastImage is not None:
        subImage = cv2.absdiff(image, pastImage)

        ret, subImage = cv2.threshold(subImage,30,255, cv2.THRESH_BINARY)
        cv2.imshow("Capture", subImage)

        if cv2.countNonZero(subImage) >= 10000 :
            send()


    if cv2.waitKey(33) >= 0:
        break
