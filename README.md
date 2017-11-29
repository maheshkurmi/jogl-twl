# jogl-twl
This Java project is [JOGL](http://www.jogamp.org)  port of [TWL:Theme Widget Library](http://twl.l33tlabs.org/)

![twl screenshot...](http://twl.l33tlabs.org/screen_simple.png)

## why did I port it
I am planning to rewrite/replace my own opengl gui library used in my project [simPHY: the 2D physics simulator](http:/simphy.com),
thats why I compared various available opengl gui libraries like nifty-gui, twl, nuklear etc and I found twl to be best suited for my use especially because of its powerful textarea andtheming capability.
Sooner or later I may use it or modify my library to incorporate these features.

## how does it differ from original twl
this jogl port of twl library intended to work with newt in jogl, so main changes madeto library are
- all keycodes, mouse events are mapped to newt  keyevents
- new jogl renderer is added
- new jogl input processor is added
- newt window and jogl animator are used to create display
- jogl effects rendered is used for twl effects
- several small tweaks to make it work with jogl

## who else can use it
Anyone planning to use full featured opengl gui in jogl (to avoid multithreading conflict on combining swing with opengl). 
[MatthiasMann](https://github.com/MatthiasMann/twl) is kind enough to freely share his awesome library with us.
For commmercial use please contact **MatthiasMann** since he is the one who holds all rights, there is no restriction in distributing or reusing code from my behalf.

## how to use it
simple download or clone this repo to create [eclipse](http://www.eclipse.org/downloads/) project(Kepler) and run some tests to check everything goes well.
You should get an idea about using twl in jogl by looking at the source.
For tutorials on using twl please visit [http://twl.l33tlabs.org/](http://twl.l33tlabs.org/).

>Let me know if it is of some use to you.


