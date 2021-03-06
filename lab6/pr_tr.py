import numpy as np

L1  = [0, 1, 1, 0, 0, 0, 0, 0, 0, 0]
#L1  = [0, 1, 1, 0, 1, 0, 0, 0, 0, 0]
L2  = [1, 0, 0, 1, 0, 0, 0, 0, 0, 0]
L3  = [0, 1, 0, 0, 0, 0, 0, 0, 0, 0]
#L3  = [0, 1, 0, 0, 0, 0, 1, 0, 0, 0]
L4  = [0, 1, 1, 0, 0, 0, 0, 0, 0, 0]
L5  = [0, 0, 0, 0, 0, 1, 1, 0, 0, 0]
L6  = [0, 0, 0, 0, 0, 0, 1, 1, 0, 0]
L7  = [0, 0, 0, 0, 1, 1, 1, 1, 1, 1]
L8  = [0, 0, 0, 0, 0, 0, 1, 0, 1, 0]
L9  = [0, 0, 0, 0, 0, 0, 1, 0, 0, 1]
L10 = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]

L = np.array([L1, L2, L3, L4, L5, L6, L7, L8, L9, L10])

ITERATIONS = 100

### TODO 1: Compute stochastic matrix M
def getM(L):
    M = np.zeros([10, 10], dtype=float)
    # number of outgoing links
    c = np.zeros([10], dtype=int)
    for i in range(10):
        c[i] = sum(L[:,i])
    for i in range(10):
        M[:,i] = L[:,i]/c[i]
    return M
    
print("Matrix L (indices)")
print(L)    

M = getM(L)

print("Matrix M (stochastic matrix)")
print(M)

### TODO 2: compute pagerank with damping factor q = 0.15
### Then, sort and print: (page index (first index = 1 add +1) : pagerank)
### (use regular array + sort method + lambda function)
print("PAGERANK")

q = 0.15
data =[]
pr = np.zeros([10], dtype=float)
for j in range(ITERATIONS):
    prOld =pr.copy()
    for i in range(len(pr)):
        #pr[i] = q+(1-q)*prOld.dot(M[i,:])
        pr[i] = q + (1 - q) * sum(M[i, :])
        if(j==ITERATIONS-1):
            data.append((i + 1, pr[i]))

data.sort(key=lambda pair: pair[1],reverse=True)
for d in data:
    print(str(d[0]) +" : " +str(d[1]) )
### TODO 3: compute trustrank with damping factor q = 0.15
### Documents that are good = 1, 2 (indexes = 0, 1)
### Then, sort and print: (page index (first index = 1, add +1) : trustrank)
### (use regular array + sort method + lambda function)
print("TRUSTRANK (DOCUMENTS 1 AND 2 ARE GOOD)")

q = 0.15

d = np.zeros([10], dtype=float)
d[0]=1
d[1]=1
tr = [v for v in d]
data =[]
for j in range(ITERATIONS):
    trOld =np.asarray(tr.copy())
    for i in range(len(tr)):
        tr[i] = q*trOld[i]+(1-q)*trOld.dot(M[i,:])
        if(j==ITERATIONS-1):
            data.append((i + 1, tr[i]))

data.sort(key=lambda pair: pair[1], reverse=True)
for d in data:
    print(str(d[0]) + " : " + str(d[1]))
### TODO 4: Repeat TODO 3 but remove the connections 3->7 and 1->5 (indexes: 2->6, 0->4) 
### before computing trustrank
