import numpy as np
from time import time
import matplotlib
matplotlib.use('Agg') #----> Specify the backend
import matplotlib.pyplot as plt

def mulLow(A, B):
     n = np.shape(A)[0]
     C = np.zeros((n,n))
     for i in range(n):
          for j in range(i+1): 
               for k in range(i-j+1):
                    C[i,j] = C[i,j] + A[i,k+j] * B[k+j,j]
     return C

def mul(A,B):
     n = np.shape(A)
     C = np.zeros(n)
     for i in range(n[0]):
          for j in range(n[0]):
               for k in range(n[0]):
                    C[i,j] += A[i,k] * B[k,j]
     return C

wtime_mulLow = np.zeros((5,2))
wtime_mul = np.zeros((5,2))

# A = np.array([[1.0, 0.0, 0.0], [7.0, 9.0, 0.0], [1.0, 3.0, 4.0]])
# B = np.array([[2.0, 0.0, 0.0], [6.0, 9.0, 0.0], [8.0, 2.0, 9.0]])
# start_time = time()
# C = mulLow(A,B)
# end_time = time()

 
# wtime_mulLow[0, 0]  = 3
# wtime_mulLow[0, 1]  = end_time - start_time

# start_time = time()
# D = mul(A,B)
# end_time = time()


# wtime_mul[0, 0]  = 3
# wtime_mul[0, 1]  = end_time - start_time


A = np.array([[1.0, 0.0, 0.0, 0.0], [2.0, 3.0, 0.0, 0.0], [4.0, 5.0, 6.0, 0.0], [7.0, 8.0, 9.0, 10.0]])
B = np.array([[5.0, 0.0, 0.0, 0.0], [9.0, 6.0, 0.0, 0.0], [4.0, 2.0, 1.0, 0.0], [10.0, 3.0, 8.0, 7.0]])
start_time = time()
C = mulLow(A,B)
end_time = time()

wtime_mulLow[1, 0]  = 4
wtime_mulLow[1, 1]  = end_time - start_time
print(C)

start_time = time()
D = mul(A,B)
end_time = time()
print(D)

wtime_mul[1, 0]  = 4
wtime_mul[1, 1]  = end_time - start_time

print(wtime_mul[1, 1]/wtime_mulLow[1, 1])

# A = np.array([[1.0, 0.0, 0.0, 0.0, 0.0], [7.0, 9.0, 0.0, 0.0, 0.0], [1.0, 3.0, 4.0, 0.0, 0.0], [1.0, 3.0, 4.0, 0.0, 0.0], [1.0, 3.0, 4.0, 0.0, 0.0]])
# B = np.array([[2.0, 0.0, 0.0, 0.0, 0.0], [6.0, 9.0, 0.0, 0.0, 0.0], [8.0, 2.0, 9.0, 0.0, 0.0], [1.0, 3.0, 4.0, 0.0, 0.0], [1.0, 3.0, 4.0, 0.0, 0.0]])
# start_time = time()
# C = mulLow(A,B)
# end_time = time() 

# wtime_mulLow[2, 0]  = 5
# wtime_mulLow[2, 1]  = end_time - start_time

# start_time = time()
# D = mul(A,B)
# end_time = time()

# wtime_mul[2, 0]  = 5
# wtime_mul[2, 1]  = end_time - start_time

# A = np.array([[1.0, 0.0, 0.0, 0.0, 0.0, 0.0], [7.0, 9.0, 0.0, 0.0, 0.0, 0.0], [1.0, 3.0, 4.0, 0.0, 0.0, 0.0], [1.0, 3.0, 4.0, 0.0, 0.0, 0.0], [7.0, 9.0, 0.0, 0.0, 0.0, 0.0], [7.0, 9.0, 0.0, 0.0, 0.0, 0.0]])
# B = np.array([[2.0, 0.0, 0.0, 0.0, 0.0, 0.0], [6.0, 9.0, 0.0, 0.0, 0.0, 0.0], [8.0, 2.0, 9.0, 0.0, 0.0, 0.0], [8.0, 2.0, 9.0, 0.0, 0.0, 0.0], [7.0, 9.0, 0.0, 0.0, 0.0, 0.0], [7.0, 9.0, 0.0, 0.0, 0.0, 0.0]])
# start_time = time()
# C = mulLow(A,B)
# end_time = time()

# wtime_mulLow[3, 0]  = 6
# wtime_mulLow[3, 1]  = end_time - start_time

# start_time = time()
# D = mul(A,B)
# end_time = time()

# wtime_mul[3, 0]  = 6
# wtime_mul[3, 1]  = end_time - start_time

# A = np.array([[1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], [7.0, 9.0, 0.0, 0.0, 0.0, 0.0, 0.0], [1.0, 3.0, 4.0, 0.0, 0.0, 0.0, 0.0], [1.0, 3.0, 4.0, 0.0, 0.0, 0.0, 0.0], [1.0, 3.0, 4.0, 0.0, 0.0, 0.0, 0.0], [1.0, 3.0, 4.0, 0.0, 0.0, 0.0, 0.0], [1.0, 3.0, 4.0, 0.0, 0.0, 0.0, 0.0]])
# B = np.array([[2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], [6.0, 9.0, 0.0, 0.0, 0.0, 0.0, 0.0], [8.0, 2.0, 9.0, 0.0, 0.0, 0.0, 0.0], [1.0, 3.0, 4.0, 0.0, 0.0, 0.0, 0.0], [1.0, 3.0, 4.0, 0.0, 0.0, 0.0, 0.0], [1.0, 3.0, 4.0, 0.0, 0.0, 0.0, 0.0], [1.0, 3.0, 4.0, 0.0, 0.0, 0.0, 0.0]])
# start_time = time()
# C = mulLow(A,B)
# end_time = time()
# print(C)

# wtime_mulLow[4, 0]  = 7
# wtime_mulLow[4, 1]  = end_time - start_time

# start_time = time()
# D = mul(A,B)
# end_time = time()
# print(D)

# wtime_mul[4, 0]  = 7
# wtime_mul[4, 1]  = end_time - start_time

# print(wtime_mul[4, 1]/wtime_mulLow[4, 1])

# plt.semilogy(wtime_mul[:, 0], wtime_mul[:, 1])
# plt.semilogy(wtime_mulLow[:, 0], wtime_mulLow[:, 1])
# plt.legend(["mul", "mulLow"])
# plt.savefig("matplotlib1.png")
