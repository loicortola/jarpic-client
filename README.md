# jarpic-client
A Simple JSON-RPC 2.0 Java Client using Jackson and OkHttp

[![Build Status](https://travis-ci.org/resourcepool/jarpic-client.svg?branch=master)](https://travis-ci.org/resourcepool/jarpic-client)

## Usage

Send single JSON-RPC request:

```java
JsonRpcClient client = new HttpJsonRpcClient(endpoint);
JsonRpcRequest req = JsonRpcRequest.builder()
  .method("cmd::execCmd")
  .param("param1", "myvalue1")
  .param("param2", "myvalue2")
  .build();
  
// With your own Result class POJO  
JsonRpcResponse<Result> res = client.send(req, Result.class);
System.out.println("Response is:");
System.out.println(res);
```

Send multiple JSON-RPC requests:

```java
JsonRpcClient client = new HttpJsonRpcClient(endpoint);
JsonRpcRequest req1 = JsonRpcRequest.builder()
  .method("cmd::execCmd")
  .param("param1", "myvalue1")
  .param("param2", "myvalue2")
  .build();

JsonRpcRequest req2 = JsonRpcRequest.builder()
  .method("cmd::resumeCmd")
  .param("key", "value")
  .build();
  
List<JsonRpcRequest> reqs = JsonRpcRequest.combine(req1, req2);
  
// With your own Result class POJO  
List<JsonRpcResponse<Result>> res = client.send(reqs, Result.class);
System.out.println("Response is:");
System.out.println(res);
```

Send single JSON-RPC Notification
```java
JsonRpcClient client = new HttpJsonRpcClient(endpoint);
JsonRpcRequest req = JsonRpcRequest.notifBuilder()
  .method("cmd::execCmd")
  .param("param1", "myvalue1")
  .param("param2", "myvalue2")
  .build();
  
// With your own Result class POJO  
JsonRpcResponse<Result> res = client.send(req, Result.class);
System.out.println("Response is:");
System.out.println(res);
```

## License
   Copyright 2016 Resourcepool

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
