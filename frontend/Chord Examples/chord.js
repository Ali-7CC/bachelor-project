fetch("test.json").then(response => response.json()).then(json => {

  // Dimensions
  const width = 1000;
  const height = 1000;
  const outerRadius = Math.min(width, height) * 0.5 - 150
  const innerRadius = outerRadius - 10

  // Data
  const matrix = json[0].matrix;
  const names = json[1].groups;


  // color
  const color = d3.scaleOrdinal(names, d3.schemeCategory10);

  // Function to draw the links
  const ribbon = d3.ribbonArrow()
    .radius(innerRadius - 1)
    .padAngle(1 / innerRadius);


  // Function to draw the nodes
  const arc = d3.arc()
    .innerRadius(innerRadius)
    .outerRadius(outerRadius);

  // Chords generator
  const chord = d3.chordDirected()
    .padAngle(10 / innerRadius)
    .sortSubgroups(d3.descending)
    .sortChords(d3.descending);

  console.log(chord);

  // ??
  // const formatValue = d3.format(".1~%");

  // d3.tickStep(start, stop, count) -> tickstep 
  const tickStep = d3.tickStep(0, d3.sum(matrix.flat()),10);
  console.log(tickStep);


  // function ticks({ startAngle, endAngle, value }) {
  //   const k = (endAngle - startAngle) / value;
  //   return d3.range(0, value, tickStep).map(value => {
  //     return { value, angle: value * k + startAngle };
  //   });
  // }


  // returns {value, angle:<angle>}
  // value ranges from 0 to the max value of the group
  // angle is the position of the tick
  function ticks(group) {
    const startAngle = group.startAngle;
    const endAngle = group.endAngle;
    const value = group.value;
    const k = (endAngle - startAngle) / value; // (arc degrees)/group value = tick position/angle?
    return d3.range(0, value, tickStep).map(value => {
      return { value, angle: value * k + startAngle };
    });

  }





  const svg = d3.select("#chord-container").append("svg")
    .attr("viewBox", [-width / 2, -height / 2, width, height]);

  const chords = chord(matrix);
  console.log(chords);


  // Nodes
  // A group that contains:
  // 1. A path
  // 2. A title with a text element(node value on hover)
  // 3.  collection of tickGroups:
  //      - Line
  //      - Text (node name) 
  // The title shows the overall value in the node
  const group = svg.append("g")
    .attr("font-size", 16)
    .attr("font-family", "sans-serif")
    .selectAll("g")
    .data(chords.groups)
    .join("g");

  group.append("path")
    .attr("fill", d => color(names[d.index]))
    .attr("d", d => arc(d));

  group.append("title")
    .text(d => d.value);



  const groupTick = group.append("g")
    .selectAll("g")
    .data(d => ticks(d))
    .join("g")
    .attr("transform", d => `rotate(${d.angle * 180 / Math.PI - 90}) translate(${outerRadius},0)`);


    
  groupTick.append("line")
    .attr("stroke", "currentColor")
    .attr("x2", 6);


    
  groupTick.append("text")
    .attr("x", 8)
    .attr("dy", "0.35em")
    .attr("transform", d => d.angle > Math.PI ? "rotate(180) translate(-16)" : null)
    .attr("text-anchor", d => d.angle > Math.PI ? "end" : null)
    .text(d => d.value);

 
    group.select("text")
    .attr("font-weight", "bold")
    .text(function (d) {
      return this.getAttribute("text-anchor") === "end"
        ? `↑ ${names[d.index]}`
        : `${names[d.index]} ↓`;
    });




  // Links
  // A group that contains a path and a title with a text element
  svg.append("g")
    .attr("fill-opacity", 0.8)
    .selectAll("path")
    .data(chords)
    .join("path")
    .style("mix-blend-mode", "multiply")
    .attr("fill", d => color(names[d.source.index]))
    .attr("d", ribbon)
    .append("title")
    .text(d => `${d.source.value} ${names[d.source.index]} -> ${names[d.target.index]}\n${d.target.value} ${names[d.target.index]} -> ${names[d.source.index]}`);




});






