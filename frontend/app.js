fetch('data.json').then(response => response.json()).then(data => {

    // Defining the width and height of the viewBox
    const width = 1000;
    const height = 700;

    // Appending an element svg to a container in the HTML
    const svg = d3.select('#sankey-container')
                .append('svg')
                .attr("viewBox", [0, 0, width, height]);


    // Create the Sankey generator with some settings
    const sankeyGenerator = d3.sankey()
                            .nodeId(d => d.name)
                            .nodeWidth(50)
                            .nodePadding(10)
                            .extent([[1, 5], [width - 1, height - 5]]);


    // Creating a graph using the generator contains the node and link positions 
    const graph = sankeyGenerator(data);



    // Creating a color scale with the range shcemeCategory10
    // The scale takes a list of input (domain) and maps it to the range
    const colorScale = d3.scaleOrdinal(d3.schemeCategory10);

    // Variables for the edge colors
    const edgeColor = "input";
    // const edgeColor = "output";


    // Creating the nodes
    // A node is a recteanlge
    // Nodes are group in one g group
    svg.append("g")
       .attr("stroke", "#000")
       .selectAll("rect")
       .data(graph.nodes)
       .join("rect")
       .attr("x", d => d.x0)
       .attr("y", d => d.y0)
       .attr("height", d => d.y1 - d.y0)
       .attr("width", d => d.x1 - d.x0)
       .attr("fill", d => colorScale(d.name))
       .append("title").text(d => `${d.name}\n${(d.value)}`);


    // Adding names to the nodes
    svg.append("g")
    .attr("font-family", "sans-serif")
    .attr("font-size", 6)
    .selectAll("text")
    .data(graph.nodes)
    .join("text")
    .attr("x", d => d.x0 < width / 2 ? d.x1 + 6 : d.x0 - 6)
    .attr("y", d => (d.y1 + d.y0) / 2)
    .attr("dy", "0.35em")
    .attr("text-anchor", d => d.x0 < width / 2 ? "start" : "end")
    .text(d => d.name);


    // Creating the links
    // A link is a group
    // The group contains a path and a title

    // Defining the group
    const link = svg.append("g")
                    .attr("fill", "none")
                    .attr("stroke-opacity", 0.5)
                    .selectAll("g")
                    .data(graph.links)
                    .join("g")
                    // .style("mix-blend-mode", "multiply");

    // Adding the path elements
    link.append("path")
        .attr("d", d3.sankeyLinkHorizontal())
        .attr("stroke", d => edgeColor === "none" ? "#aaa"
                            : edgeColor === "input" ? colorScale(d.source.name) 
                            : color(d.target))
        .attr("stroke-width", d => Math.max(1, d.width));

    // Adding the title element (on hover)
    link.append("title")
        .text(d => `${d.source.name} → ${d.target.name}\n${(d.value)}`);


});













