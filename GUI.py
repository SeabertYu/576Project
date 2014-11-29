__author__ = 'Boyang'
from Tkinter import *
from PIL import Image, ImageTk
CANVAS_WIDTH = 72
CANVAS_HEIGHT = 88
JPG_FOLDER = "./JPG/"

def getCanvas(top, row, col, img):
    c = Canvas(top, width = CANVAS_WIDTH, height = CANVAS_HEIGHT)
    c.grid(row=row, column=col)
    image = Image.open(img)
    pImage = ImageTk.PhotoImage(image)
    c.create_image(image=pImage)

top = Tk()
top.title("Image Explorer")
getCanvas(top, 1, 1, JPG_FOLDER+"image001.jpg")
top.mainloop()