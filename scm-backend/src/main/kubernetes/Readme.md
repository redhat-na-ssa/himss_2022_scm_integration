## Prerequisites
Install the `KEDA` Operator from OperatorHub, and create a `KedaController` (see below) in the generated keda namespace

```
apiVersion: keda.sh/v1alpha1
kind: KedaController
metadata:
  namespace: keda
  name: keda
spec:
  logLevel: info
  logLevelMetrics: '0'
  watchNamespace: ''
  logEncoder: console
```