;;;;;;;;;;;;;;;;;;
;;;;; BREEDS ;;;;;
;;;;;;;;;;;;;;;;;;

breed [ NeedSignalCancerCells NeedSignalCancerCell ]
breed [ BlockedApoCancerCells BlockedApoCancerCell ]
breed [ EarlyApoCancerCells EarlyApoCancerCell ]
breed [ LateApoCancerCells LateApoCancerCell ]
breed [ DeadCancerCells DeadApoCancerCell ]
breed [ Monocytes Monocyte ]
breed [ NurseLikeCells NurseLikeCell ]



;;;;;;;;;;;;;;;;;;;;;
;;;;; VARIABLES ;;;;;
;;;;;;;;;;;;;;;;;;;;;

globals [
  ;; create limits of the world
  z-max
  coef

  ;; global variables for headless mode
  simulation-duration
  prop-world-filled-init
  nb-monocytes-init
  nb-cancer-cells-init
  prop-monocytes-init
  prop-apoptotic-init
  BlockedApoptosisThreshold
  Chemical_B_threshold
  EarlyApoThreshold
  LateApoThreshold
  DeathThreshold
]

patches-own [
  forbidden ;; manual fix to prevent auto-wrapping
  Chemical_A ;; chemical excreted by Monocytes and NurseLikeCells
  Chemical_B ;; chemical excreted by NeedSignalCancerCells
]

NeedSignalCancerCells-own[
  Chemical_N ;; accumulated necrotic factors
  Chemical_S ;; accumulated survival signals
]

BlockedApoCancerCells-own[
  Chemical_S ;; accumulated survival signals
]

EarlyApoCancerCells-own[
  Chemical_N ;; accumulated necrotic factors
  Chemical_S ;; accumulated survival signals
]

LateApoCancerCells-own[
  Chemical_N ;; accumulated necrotic factors
  Chemical_S ;; accumulated survival signals
]

DeadCancerCells-own[
  Chemical_N ;; accumulated necrotic factors
  Chemical_S ;; accumulated survival signals
]

Monocytes-own[
  SignalStrength
]

NurseLikeCells-own[
  SignalStrength
]


;;;;;;;;;;;;;;;;;
;;;;; SETUP ;;;;;
;;;;;;;;;;;;;;;;;

to setup
  clear-all
  reset-ticks
  let seed 12
  random-seed seed

  setup-Globals

  setup-World
  setup-Monocytes
  setup-NeedSignalCancerCells

end



to setup-Globals
  set prop-world-filled-init gui-prop-world-filled-init
  set nb-cancer-cells-init gui-nb-cancer-cells-init
  set prop-monocytes-init gui-prop-monocytes-init
  set prop-apoptotic-init gui-prop-apoptotic-init

  ;; NeedSignalCancerCells need to accumulate some amount of Chemical_A anti-apoptotic signal from Monocytes and/or NLCs to enter BlockedApoptosis
  set BlockedApoptosisThreshold gui-BlockedApoptosisThreshold

  ;; Monocytes need to accumulate some amount of Chemical_B from NeedSignalCancerCells cancer cells to increase their SignalStrength and eventually differentiate into NLCs
  set Chemical_B_threshold gui-NLC_threshold

  ;; NeedSignalCancerCells turn into EarlyApoCancerCells when they reach necrotic value 1
  set EarlyApoThreshold gui-EarlyApoThreshold

  ;; EarlyApoCancerCells turn into LateApoCancerCells when they reach necrotic value 2
  set LateApoThreshold gui-LateApoThreshold

  ;; LateApoCancerCells turn into DeadCancerCells when they reach necrotic value 3
  set DeathThreshold gui-DeathThreshold

end


to setup-World
  ;; get initial number of cells
  set nb-monocytes-init ceiling (prop-monocytes-init * nb-cancer-cells-init / 100)
  let nb-cells-init nb-cancer-cells-init + nb-monocytes-init

  ;; resize the simulation world
  set z-max 1
  set coef 4
  let world-radius ceiling sqrt (nb-cells-init * 100 / (prop-world-filled-init * pi * (z-max * 2 + 1)))
  resize-world (- world-radius * coef) (world-radius * coef) (- world-radius * coef) (world-radius * coef) (- z-max * coef) (z-max * coef)

  ;; close the world
  ;; only not forbidden patches are accessible to the agents
  ask patches [
    ifelse ((pxcor * pxcor + pycor * pycor) > (world-radius * world-radius) or abs pzcor > z-max)
    [ set forbidden true ]
    [ set forbidden false
      set Chemical_A 0
      set Chemical_B 0
      ;;recolor-patch
    ]
  ]

   ;; create NeedSignalCancerCells
  ask n-of nb-cells-init (patches with [not forbidden]) [ sprout-NeedSignalCancerCells 1 ]

end

to recolor-patch  ;; patch procedure
  ;; give color to chemical sources
  ;; scale color to show chemical concentration
  set pcolor scale-color green Chemical_A 0 100
  set pcolor scale-color red Chemical_B 0 100
end

to setup-Monocytes
  ;; create Monocytes
  ask n-of nb-monocytes-init turtles
  [
    set breed Monocytes
    set Chemical_A 0
    set SignalStrength 0
    set color blue
    set shape "square"
  ]
end

to setup-NeedSignalCancerCells
  ask NeedSignalCancerCells [
    set Chemical_B 0
    set Chemical_S 0
    set Chemical_N 0
    set color red
    set shape "default"
    set size 1
  ]

  ;; create some EarlyApoCancerCells
  ask n-of (prop-apoptotic-init * nb-cancer-cells-init / 100) NeedSignalCancerCells
  [
  set breed EarlyApoCancerCells
    set Chemical_N 10
    set Chemical_B 0
    set Chemical_S 0
    set color orange
    set shape "default"
  ]
  end




;;;;;;;;;;;;;;
;;;;; GO ;;;;;
;;;;;;;;;;;;;;



to go
  reset-timer
  ifelse (count NeedSignalCancerCells + count EarlyApoCancerCells + count LateApoCancerCells > 0)
  [
    common-go-procedures
    tick
  ]
  [ stop ]
end

to common-go-procedures ;; between headless and gui modes

  ;; NeedSignalCancerCells actions
  ;; BlockedApoCancerCells actions
  ;; EarlyApoCancerCells actions
  ;; LateApoCancerCells actions
  ;; Monocytes actions
  ;; NurseLikeCells actions


  update-positions

  ;; Monocytes actions
  ask Monocytes 
  [
    phagocyte
  ]

  update-chemicals

  update-breeds

end

to update-positions

  ;; NeedSignalCancerCells actions
  ;; BlockedApoCancerCells actions
  ;; EarlyApoCancerCells actions
  ;; LateApoCancerCells actions
  ;; Monocytes actions
  ;; NurseLikeCells actions

  ;; NeedSignalCancerCells actions
  ask NeedSignalCancerCells
  [
    move-towards-chemical_A
  ]

  ;; BlockedApoCancerCells actions
  ask BlockedApoCancerCells 
  [
    move 6 nobody 
  ]

  ;; EarlyApoCancerCells actions
  ask EarlyApoCancerCells
  [
    move-towards-chemical_A
  ]

  ;; LateApoCancerCells actions
  ask LateApoCancerCells
  [
    move-towards-chemical_A
  ]

  ;; Monocytes actions
  ask Monocytes
  [ move 1 nobody ]

  ;; NurseLikeCells actions
  ask NurseLikeCells 
  [ if (random 100 < 50) [ move 1 nobody ]]

end

to update-chemicals

  ;; Patches actions
  diffuse chemical_A (diffusion-rate / 100)
  diffuse chemical_B (diffusion-rate / 100)
  ask patches
  [
    set chemical_A chemical_A * (100 - evaporation-rate) / 100  ;; slowly evaporate chemical_A
    set chemical_B chemical_B * (100 - evaporation-rate) / 100  ;; slowly evaporate chemical_B
    ;;recolor-patch
  ]

  ;; NeedSignalCancerCells actions
  ask NeedSignalCancerCells
  [
    set Chemical_B Chemical_B + 1 ;; drop some Chemical_B
    set Chemical_N Chemical_N + 1 ;; accumulate some Chemical_N
    set Chemical_S Chemical_S + Chemical_A ;; accumulate some Chemical_A into Chemical_S to confer and increase survival (anti-apoptotic behavior), proxy : Chemical_S - we might need to substract the Chemical_A from the patch
  ]

  ;; BlockedApoCancerCells actions
  ask BlockedApoCancerCells
  [
    ;; as long as they walk on patches with some Chemical_A, no change; but when they walk on an empty patch, their Chemical_S decreases.
  ]

  ;; EarlyApoCancerCells actions
  ask EarlyApoCancerCells
  [
    set Chemical_S Chemical_S + Chemical_A ;; accumulate some Chemical_A into Chemical_S to confer and increase survival (anti-apoptotic behavior), proxy : Chemical_S - we might need to substract the Chemical_A from the patch
    set Chemical_N Chemical_N + 1 ;; accumulate some Chemical_N
  ]

  ;; LateApoCancerCells actions
  ask LateApoCancerCells
  [
    set Chemical_N Chemical_N + 1 ;; accumulate some Chemical_N
  ]

  ;; Monocytes actions
  ask Monocytes
  [
    set SignalStrength SignalStrength + Chemical_B ;; update SignalStrength based on Chemical_B, we might need to substract this Chemical_B amount from the patch
    set Chemical_A Chemical_A + SignalStrength / 10  ;; drop some Chemical_A
  ]

  ;; NurseLikeCells actions

end

to update-breeds
  ;; NeedSignalCancerCells
  ask NeedSignalCancerCells
  [
    if ( Chemical_N >= EarlyApoThreshold )
    [
      ;; change from NeedSignalCancerCell to EarlyApoCancerCell
      set breed EarlyApoCancerCells
      set color orange
      set shape "default"
      set Chemical_N Chemical_N
      set Chemical_S Chemical_S
    ]
  ]

  ;; EarlyApoCancerCells
  ask EarlyApoCancerCells
  [
    if ( Chemical_N >= LateApoThreshold )
    [
      ;; change from EarlyApoCancerCell to LateApoCancerCell
      set breed LateApoCancerCells
      set color yellow
      set shape "default"
      set Chemical_N Chemical_N
      set Chemical_S 0
    ]
  ]

  ;; LateApoCancerCells
  ask LateApoCancerCells
  [
    if ( Chemical_N >= DeathThreshold )
    [
      ;; change from LateApoCancerCell to DeadCancerCell
      set breed DeadCancerCells
      set color grey
      set shape "default"
      set Chemical_N Chemical_N
      set Chemical_S 0
    ]
  ]

  ;; BlockedApoCancerCells
  ask BlockedApoCancerCells
  [
    if ( Chemical_S <= BlockedApoptosisThreshold )
    [
      ;; change from BlockedApoCancerCell to NeedSignalCancerCell
      set breed NeedSignalCancerCells
      set color red
      set shape "default"
      set Chemical_N 0
      set Chemical_S 0
    ]
  ]

  ;; Monocytes
  ask Monocytes
  [
    if ( SignalStrength >= gui-NLC_threshold )
    [
      ;; change from Monocyte to NurseLikeCells
      set breed NurseLikeCells
      set color green
      set shape "square"
      set Chemical_A Chemical_A
    ]
  ]
end


;;UTILS

to phagocyte
    ;; look for dead or late apoptotic cells in the neighbors
    let debris (turtle-set (DeadCancerCells-on neighbors) ([LateApoCancerCells] of neighbors))
    ifelse (any? debris)
    ;; phagocyte
    [ ask one-of debris [die] ]
end

to move [steps goal] ;; agent procedure
  ifelse (goal != nobody)
  ;; move towards goal
  [
    while [steps > 0] [
      ;; distance from the agent to the goal
      let current-distance distance goal
      ;; find neighbors who are closer to the goal
      let closer-positions neighbors with [not forbidden and
                                           not any? turtles-here and
                                           distance goal <= current-distance and
                                           distance goal > 1]
      ifelse (any? closer-positions)
      ;; move closer to the goal
      [
        move-to min-one-of closer-positions [distance goal]
        set steps steps - 1
      ]
      ;; impossible to move closer, stay put
      [ set steps 0 ]
    ]
  ]
  ;; random move
  [
    while [steps > 0] [
      ;; find accessible positions
      let possible-positions neighbors with [not forbidden and not any? turtles-here]
      ifelse (any? possible-positions)
      ;; move
      [
        move-to one-of possible-positions
        set steps steps - 1
      ]
      ;; impossible to move closer, stay put
      [ set steps 0 ]
    ]
  ]
end

to move-towards-Chemical_A ;; cancer cell procedure
  ;; sniff left and right, and go where the strongest smell is
  let scent-ahead chemical_A-scent-at-angle   0
  let scent-right chemical_A-scent-at-angle  45
  let scent-left  chemical_A-scent-at-angle -45
  if (scent-right > scent-ahead) or (scent-left > scent-ahead)
  [ ifelse scent-right > scent-left
    [ rt 45 ]
    [ lt 45 ] ]
end

to-report chemical_A-scent-at-angle [angle]
  let p patch-right-and-ahead angle 1
  if p = nobody [ report 0 ]
  report [Chemical_A] of p
end


@#$#@#$#@
GRAPHICS-WINDOW
0
0
733
734
-1
-1
5.0
1
10
1
1
1
0
1
1
1
-72
72
-72
72
-4
4
1
1
1
ticks
30.0

BUTTON
8
10
81
43
NIL
setup
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

INPUTBOX
8
86
169
146
gui-nb-cancer-cells-init
2500.0
1
0
Number

BUTTON
99
42
186
75
go once
go
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

MONITOR
2
581
90
626
cancer cells
count cancer-cells
17
1
11

TEXTBOX
218
18
335
39
model
15
15.0
1

MONITOR
2
626
90
671
monocytes
count monocytes with [SignalStrength < 100]
17
1
11

MONITOR
3
671
91
716
NLCs
count monocytes with [SignalStrength >= 100]
17
1
11

BUTTON
8
42
81
75
NIL
go
T
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

MONITOR
2
536
91
581
NIL
count turtles
17
1
11

MONITOR
3
761
109
806
allowed patches
count patches with [not forbidden]
17
1
11

PLOT
378
10
1167
381
time series
time
nb of cells
0.0
10.0
0.0
10.0
true
true
"" ""
PENS
"dead-cells" 1.0 0 -7500403 true "" "plot count dead-cells"
"cancer-cell looking for NLC signals" 1.0 0 -2674135 true "" "plot count cancer-cells with [not early-apoptotic? and not late-apoptotic? and not blocked-apoptosis?]"
"early apoptotic" 1.0 0 -955883 true "" "plot count cancer-cells with [early-apoptotic?]"
"late apoptotic" 1.0 0 -7171555 true "" "plot count cancer-cells with [late-apoptotic?]"
"total cancer cells" 1.0 0 -16777216 true "" "plot count cancer-cells"
"blocked apoptosis" 1.0 0 -2064490 true "" "plot count cancer-cells with [blocked-apoptosis?]"
"late + dead = debris" 1.0 0 -6459832 true "" "plot count (cancer-cells with [late-apoptotic?]) + count (dead-cells)"

MONITOR
3
716
91
761
dead cells
count dead-cells
17
1
11

MONITOR
91
582
223
627
apoptotic
count cancer-cells with [early-apoptotic? or late-apoptotic?]
17
1
11

MONITOR
91
537
223
582
blocked-apoptosis
count cancer-cells with [blocked-apoptosis?]
17
1
11

INPUTBOX
8
145
169
205
gui-prop-monocytes-init
1.0
1
0
Number

INPUTBOX
8
204
169
264
gui-prop-apoptotic-init
6.0
1
0
Number

INPUTBOX
168
322
355
382
gui-DeathThreshold
4.0
1
0
Number

BUTTON
99
10
186
43
go-stop
while [ticks < gui-simulation-duration * 24] [ go ]\nvid:save-recording \"film_test.mp4\"
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

INPUTBOX
168
86
329
146
gui-prop-world-filled-init
85.5
1
0
Number

INPUTBOX
8
382
169
442
gui-simulation-duration
13.0
1
0
Number

PLOT
376
380
1016
516
monocytes
NIL
NIL
0.0
10.0
0.0
10.0
true
true
"" ""
PENS
"monocytes" 1.0 0 -13345367 true "" "plot count monocytes with [SignalStrength < 100]"
"NLCs" 1.0 0 -10899396 true "" "plot count monocytes with [SignalStrength >= 100]"

MONITOR
91
627
222
672
early-apoptotic
count cancer-cells with [early-apoptotic?]
17
1
11

MONITOR
90
672
220
717
late-apoptotic
count cancer-cells with [late-apoptotic?]
17
1
11

MONITOR
3
805
77
850
ticks
ticks / 24
1
1
11

MONITOR
223
582
339
627
apoptotic-duration
(((sum [LateApoptoticDuration] of cancer-cells with [late-apoptotic?]) / (count cancer-cells with [late-apoptotic?])) - ticks)\n+\n(((sum [EarlyApoptoticDuration] of cancer-cells with [early-apoptotic?]) / (count cancer-cells with [early-apoptotic?])) - ticks)
2
1
11

INPUTBOX
0
260
189
320
gui-NLC_threshold
0.0
1
0
Number

INPUTBOX
169
205
359
265
gui-EarlyApoThreshold
0.0
1
0
Number

INPUTBOX
168
147
329
207
gui-BlockedApoptosisThreshold
0.0
1
0
Number

PLOT
376
515
935
777
viability & survival rate
time
%
0.0
10.0
0.0
100.0
true
true
"" ""
PENS
"Viability" 1.0 0 -16777216 true "" "plot \n(100 - \n( \n(count(dead-cells)/ (count(cancer-cells) + count(dead-cells))) * 100\n)\n)"
"Survival" 1.0 0 -11221820 true "" "plot  ((count cancer-cells / nb-cancer-cells-init)) * 100"
"Remaining Cell Ratio" 1.0 0 -2674135 true "" "plot \n(\n( \n((count(cancer-cells) + count(dead-cells)) / nb-cancer-cells-init) * 100\n)\n)"

MONITOR
77
805
176
850
Average Signal
(sum ([SignalStrength] of monocytes) )/ (count monocytes)
2
1
11

INPUTBOX
189
266
404
326
gui-LateApoThreshold
0.0
1
0
Number

SLIDER
243
395
415
428
diffusion-rate
diffusion-rate
0
100
50.0
1
1
NIL
HORIZONTAL

SLIDER
286
429
458
462
evaporation-rate
evaporation-rate
0
100
50.0
1
1
NIL
HORIZONTAL

@#$#@#$#@
## WHAT IS IT?

(a general understanding of what the model is trying to show or explain)

## HOW IT WORKS

(what rules the agents use to create the overall behavior of the model)

## HOW TO USE IT

(how to use the model, including a description of each of the items in the Interface tab)

## THINGS TO NOTICE

(suggested things for the user to notice while running the model)

## THINGS TO TRY

(suggested things for the user to try to do (move sliders, switches, etc.) with the model)

## EXTENDING THE MODEL

(suggested things to add or change in the Code tab to make the model more complicated, detailed, accurate, etc.)

## NETLOGO FEATURES

(interesting or unusual features of NetLogo that the model uses, particularly in the Code tab; or where workarounds were needed for missing features)

## RELATED MODELS

(models in the NetLogo Models Library and elsewhere which are of related interest)

## CREDITS AND REFERENCES

(a reference to the model's URL on the web if it has one, as well as any other necessary credits, citations, and links)
@#$#@#$#@
default
true
0
Polygon -7500403 true true 150 5 40 250 150 205 260 250

airplane
true
0
Polygon -7500403 true true 150 0 135 15 120 60 120 105 15 165 15 195 120 180 135 240 105 270 120 285 150 270 180 285 210 270 165 240 180 180 285 195 285 165 180 105 180 60 165 15

arrow
true
0
Polygon -7500403 true true 150 0 0 150 105 150 105 293 195 293 195 150 300 150

box
false
0
Polygon -7500403 true true 150 285 285 225 285 75 150 135
Polygon -7500403 true true 150 135 15 75 150 15 285 75
Polygon -7500403 true true 15 75 15 225 150 285 150 135
Line -16777216 false 150 285 150 135
Line -16777216 false 150 135 15 75
Line -16777216 false 150 135 285 75

bug
true
0
Circle -7500403 true true 96 182 108
Circle -7500403 true true 110 127 80
Circle -7500403 true true 110 75 80
Line -7500403 true 150 100 80 30
Line -7500403 true 150 100 220 30

butterfly
true
0
Polygon -7500403 true true 150 165 209 199 225 225 225 255 195 270 165 255 150 240
Polygon -7500403 true true 150 165 89 198 75 225 75 255 105 270 135 255 150 240
Polygon -7500403 true true 139 148 100 105 55 90 25 90 10 105 10 135 25 180 40 195 85 194 139 163
Polygon -7500403 true true 162 150 200 105 245 90 275 90 290 105 290 135 275 180 260 195 215 195 162 165
Polygon -16777216 true false 150 255 135 225 120 150 135 120 150 105 165 120 180 150 165 225
Circle -16777216 true false 135 90 30
Line -16777216 false 150 105 195 60
Line -16777216 false 150 105 105 60

car
false
0
Polygon -7500403 true true 300 180 279 164 261 144 240 135 226 132 213 106 203 84 185 63 159 50 135 50 75 60 0 150 0 165 0 225 300 225 300 180
Circle -16777216 true false 180 180 90
Circle -16777216 true false 30 180 90
Polygon -16777216 true false 162 80 132 78 134 135 209 135 194 105 189 96 180 89
Circle -7500403 true true 47 195 58
Circle -7500403 true true 195 195 58

circle
false
0
Circle -7500403 true true 0 0 300

circle 2
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240

cow
false
0
Polygon -7500403 true true 200 193 197 249 179 249 177 196 166 187 140 189 93 191 78 179 72 211 49 209 48 181 37 149 25 120 25 89 45 72 103 84 179 75 198 76 252 64 272 81 293 103 285 121 255 121 242 118 224 167
Polygon -7500403 true true 73 210 86 251 62 249 48 208
Polygon -7500403 true true 25 114 16 195 9 204 23 213 25 200 39 123

cylinder
false
0
Circle -7500403 true true 0 0 300

dot
false
0
Circle -7500403 true true 90 90 120

face happy
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 255 90 239 62 213 47 191 67 179 90 203 109 218 150 225 192 218 210 203 227 181 251 194 236 217 212 240

face neutral
false
0
Circle -7500403 true true 8 7 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Rectangle -16777216 true false 60 195 240 225

face sad
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 168 90 184 62 210 47 232 67 244 90 220 109 205 150 198 192 205 210 220 227 242 251 229 236 206 212 183

fish
false
0
Polygon -1 true false 44 131 21 87 15 86 0 120 15 150 0 180 13 214 20 212 45 166
Polygon -1 true false 135 195 119 235 95 218 76 210 46 204 60 165
Polygon -1 true false 75 45 83 77 71 103 86 114 166 78 135 60
Polygon -7500403 true true 30 136 151 77 226 81 280 119 292 146 292 160 287 170 270 195 195 210 151 212 30 166
Circle -16777216 true false 215 106 30

flag
false
0
Rectangle -7500403 true true 60 15 75 300
Polygon -7500403 true true 90 150 270 90 90 30
Line -7500403 true 75 135 90 135
Line -7500403 true 75 45 90 45

flower
false
0
Polygon -10899396 true false 135 120 165 165 180 210 180 240 150 300 165 300 195 240 195 195 165 135
Circle -7500403 true true 85 132 38
Circle -7500403 true true 130 147 38
Circle -7500403 true true 192 85 38
Circle -7500403 true true 85 40 38
Circle -7500403 true true 177 40 38
Circle -7500403 true true 177 132 38
Circle -7500403 true true 70 85 38
Circle -7500403 true true 130 25 38
Circle -7500403 true true 96 51 108
Circle -16777216 true false 113 68 74
Polygon -10899396 true false 189 233 219 188 249 173 279 188 234 218
Polygon -10899396 true false 180 255 150 210 105 210 75 240 135 240

house
false
0
Rectangle -7500403 true true 45 120 255 285
Rectangle -16777216 true false 120 210 180 285
Polygon -7500403 true true 15 120 150 15 285 120
Line -16777216 false 30 120 270 120

leaf
false
0
Polygon -7500403 true true 150 210 135 195 120 210 60 210 30 195 60 180 60 165 15 135 30 120 15 105 40 104 45 90 60 90 90 105 105 120 120 120 105 60 120 60 135 30 150 15 165 30 180 60 195 60 180 120 195 120 210 105 240 90 255 90 263 104 285 105 270 120 285 135 240 165 240 180 270 195 240 210 180 210 165 195
Polygon -7500403 true true 135 195 135 240 120 255 105 255 105 285 135 285 165 240 165 195

line
true
0
Line -7500403 true 150 0 150 300

line half
true
0
Line -7500403 true 150 0 150 150

pentagon
false
0
Polygon -7500403 true true 150 15 15 120 60 285 240 285 285 120

person
false
0
Circle -7500403 true true 110 5 80
Polygon -7500403 true true 105 90 120 195 90 285 105 300 135 300 150 225 165 300 195 300 210 285 180 195 195 90
Rectangle -7500403 true true 127 79 172 94
Polygon -7500403 true true 195 90 240 150 225 180 165 105
Polygon -7500403 true true 105 90 60 150 75 180 135 105

plant
false
0
Rectangle -7500403 true true 135 90 165 300
Polygon -7500403 true true 135 255 90 210 45 195 75 255 135 285
Polygon -7500403 true true 165 255 210 210 255 195 225 255 165 285
Polygon -7500403 true true 135 180 90 135 45 120 75 180 135 210
Polygon -7500403 true true 165 180 165 210 225 180 255 120 210 135
Polygon -7500403 true true 135 105 90 60 45 45 75 105 135 135
Polygon -7500403 true true 165 105 165 135 225 105 255 45 210 60
Polygon -7500403 true true 135 90 120 45 150 15 180 45 165 90

sheep
false
15
Circle -1 true true 203 65 88
Circle -1 true true 70 65 162
Circle -1 true true 150 105 120
Polygon -7500403 true false 218 120 240 165 255 165 278 120
Circle -7500403 true false 214 72 67
Rectangle -1 true true 164 223 179 298
Polygon -1 true true 45 285 30 285 30 240 15 195 45 210
Circle -1 true true 3 83 150
Rectangle -1 true true 65 221 80 296
Polygon -1 true true 195 285 210 285 210 240 240 210 195 210
Polygon -7500403 true false 276 85 285 105 302 99 294 83
Polygon -7500403 true false 219 85 210 105 193 99 201 83

square
false
0
Rectangle -7500403 true true 30 30 270 270

square 2
false
0
Rectangle -7500403 true true 30 30 270 270
Rectangle -16777216 true false 60 60 240 240

star
false
0
Polygon -7500403 true true 151 1 185 108 298 108 207 175 242 282 151 216 59 282 94 175 3 108 116 108

target
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240
Circle -7500403 true true 60 60 180
Circle -16777216 true false 90 90 120
Circle -7500403 true true 120 120 60

tree
false
0
Circle -7500403 true true 118 3 94
Rectangle -6459832 true false 120 195 180 300
Circle -7500403 true true 65 21 108
Circle -7500403 true true 116 41 127
Circle -7500403 true true 45 90 120
Circle -7500403 true true 104 74 152

triangle
false
0
Polygon -7500403 true true 150 30 15 255 285 255

triangle 2
false
0
Polygon -7500403 true true 150 30 15 255 285 255
Polygon -16777216 true false 151 99 225 223 75 224

truck
false
0
Rectangle -7500403 true true 4 45 195 187
Polygon -7500403 true true 296 193 296 150 259 134 244 104 208 104 207 194
Rectangle -1 true false 195 60 195 105
Polygon -16777216 true false 238 112 252 141 219 141 218 112
Circle -16777216 true false 234 174 42
Rectangle -7500403 true true 181 185 214 194
Circle -16777216 true false 144 174 42
Circle -16777216 true false 24 174 42
Circle -7500403 false true 24 174 42
Circle -7500403 false true 144 174 42
Circle -7500403 false true 234 174 42

turtle
true
0
Polygon -10899396 true false 215 204 240 233 246 254 228 266 215 252 193 210
Polygon -10899396 true false 195 90 225 75 245 75 260 89 269 108 261 124 240 105 225 105 210 105
Polygon -10899396 true false 105 90 75 75 55 75 40 89 31 108 39 124 60 105 75 105 90 105
Polygon -10899396 true false 132 85 134 64 107 51 108 17 150 2 192 18 192 52 169 65 172 87
Polygon -10899396 true false 85 204 60 233 54 254 72 266 85 252 107 210
Polygon -7500403 true true 119 75 179 75 209 101 224 135 220 225 175 261 128 261 81 224 74 135 88 99

wheel
false
0
Circle -7500403 true true 3 3 294
Circle -16777216 true false 30 30 240
Line -7500403 true 150 285 150 15
Line -7500403 true 15 150 285 150
Circle -7500403 true true 120 120 60
Line -7500403 true 216 40 79 269
Line -7500403 true 40 84 269 221
Line -7500403 true 40 216 269 79
Line -7500403 true 84 40 221 269

wolf
false
0
Polygon -16777216 true false 253 133 245 131 245 133
Polygon -7500403 true true 2 194 13 197 30 191 38 193 38 205 20 226 20 257 27 265 38 266 40 260 31 253 31 230 60 206 68 198 75 209 66 228 65 243 82 261 84 268 100 267 103 261 77 239 79 231 100 207 98 196 119 201 143 202 160 195 166 210 172 213 173 238 167 251 160 248 154 265 169 264 178 247 186 240 198 260 200 271 217 271 219 262 207 258 195 230 192 198 210 184 227 164 242 144 259 145 284 151 277 141 293 140 299 134 297 127 273 119 270 105
Polygon -7500403 true true -1 195 14 180 36 166 40 153 53 140 82 131 134 133 159 126 188 115 227 108 236 102 238 98 268 86 269 92 281 87 269 103 269 113

x
false
0
Polygon -7500403 true true 270 75 225 30 30 225 75 270
Polygon -7500403 true true 30 75 75 30 270 225 225 270
@#$#@#$#@
NetLogo 3D 6.1.0
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
<experiments>
  <experiment name="first-explo" repetitions="1" runMetricsEveryStep="true">
    <setup>setup</setup>
    <go>go</go>
    <metric>count cancer-cells</metric>
    <metric>count monocytes</metric>
    <metric>count NLCs</metric>
    <metric>count macrophages</metric>
    <steppedValueSet variable="signal-radius" first="3" step="1" last="5"/>
    <steppedValueSet variable="proba-differentiation" first="1" step="10" last="100"/>
    <steppedValueSet variable="proba-diff-into-NLC" first="1" step="10" last="100"/>
    <steppedValueSet variable="max-duration-attachment" first="0" step="5" last="20"/>
    <steppedValueSet variable="max-duration-free-move" first="0" step="5" last="20"/>
  </experiment>
</experiments>
@#$#@#$#@
@#$#@#$#@
default
0.0
-0.2 0 0.0 1.0
0.0 1 1.0 0.0
0.2 0 0.0 1.0
link direction
true
0
Line -7500403 true 150 150 90 180
Line -7500403 true 150 150 210 180
@#$#@#$#@
0
@#$#@#$#@
