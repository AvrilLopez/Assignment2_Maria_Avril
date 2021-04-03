# This file was created and used for CSCI2072
def iteration(x, a, k_max, epsX, epsY):

     y = map(x, a)                           

     # Iterate to find successive approximations
     for k in range(k_max):
          x = y
          y = map(x,a)
          err = abs(y - x)
          res = abs(residual(x,a))
          if err < epsX and res < epsY:
               break

     return k, y, err, res                          

# Map that produces iterates converging to sqrt(a)
def map(x, a):
    return 0.5 * ( x + (a/x) )

def residual(x, a):
    return x**2 - a

a = []
for i in range(2, 101):
     a.append(float(i))

for i in range(0,100):
     k,aprox,error,residual = iteration(1., float(a[i]), 100, 1e-13, 1e-14)
     print("a = " + str(a[i]) + " , iterations = " + str(k) +", approximation = " + str(aprox) + ", error = " + str(error) + ", residual = " + str(residual))
