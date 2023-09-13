```
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta http-equiv="X-UA-Compatible" content="ie=edge" />
    <title>Static Template</title>
    <script src="https://unpkg.com/h3-js"></script>
    <script src="https://unpkg.com/@turf/turf@6.3.0/turf.min.js"></script>

    <script
      type="text/javascript"
      src="https://webapi.amap.com/maps?v=1.4.15&key=b705b0ffe322148bbf5c1febdf47fe95"
    ></script>

    <style>
      #container {
        width: 600px;
        height: 380px;
      }
    </style>
  </head>
  <body>
    <div id="container">123</div>
  </body>

  <script>
    var point = turf.point([116.397428, 39.90923]);
    var buffered = turf.buffer(point, 1, { units: "kilometers" });
    let data = buffered.geometry.coordinates[0];
    let length = data.length;
    let newdata = [];
    for (let i = 0; i < length; i++) {
      let lon = data[i][0];
      let lat = data[i][1];
      newdata.push([lat, lon]);
    }

    var map = new AMap.Map("container", {
      zoom: 11,
      center: [116.397428, 39.90923]
    });

    const hexagons = h3.compact(h3.polyfill(newdata, 11));
    //const hexagons = h3.polyfill(newdata, 11);
    console.log(hexagons.length);
    for (var i = 0; i < hexagons.length; i++) {
      // console.log(hexagons[i]);
      let h = hexagons[i];
      //let point = h3.h3ToGeo(h);
      let hex = h3.h3ToGeoBoundary(h);
      if (i < 10) {
        //console.log(point);
        //console.log(hex[0]);
      }
      // var circle = new AMap.Circle({
      //   center: new AMap.LngLat(point[1], point[0]), // 圆心位置
      //   radius: 20, // 圆半径
      //   fillColor: "red", // 圆形填充颜色
      //   strokeColor: "#fff", // 描边颜色
      //   strokeWeight: 2 // 描边宽度
      // });

      // map.add(circle);

      var path = [
        new AMap.LngLat(hex[0][1], hex[0][0]),
        new AMap.LngLat(hex[1][1], hex[1][0]),
        new AMap.LngLat(hex[2][1], hex[2][0]),
        new AMap.LngLat(hex[3][1], hex[3][0]),
        new AMap.LngLat(hex[4][1], hex[4][0]),
        new AMap.LngLat(hex[5][1], hex[5][0])
      ];

      var polygon = new AMap.Polygon({
        path: path,
        fillColor: "#fff", // 多边形填充颜色
        borderWeight: 2, // 线条宽度，默认为 1
        strokeColor: "red" // 线条颜色
      });

      map.add(polygon);
    }
  </script>
</html>

```