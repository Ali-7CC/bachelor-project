
// -------------------- Funtions --------------------


// ***** NODES *****:

// creates a group with some attributes.
// This group will contain the rect elements that forms the nodes.
const createNodeGroup = () => {
    const nodeGroup = svg.append("g")
        .attr("stroke", "#000")
        .attr("stroke-width", 1);
    return nodeGroup;

};


// Takes the node group element and adds rects by joining the data.
const updateRects = (nodeGroup, generatedData) => {
    nodeGroup.selectAll("rect")
        .data(generatedData.nodes)
        .join("rect")
        .transition()
        .duration(700)
        .attr("x", d => d.x0)
        .attr("y", d => d.y0)
        .attr("height", d => d.y1 - d.y0)
        .attr("width", d => d.x1 - d.x0)
        .attr("fill", d => colorScale(d.name.split("_")[0]));


    // Removing old title elements before appending.
    nodeGroup.selectAll("rect").selectAll("title").remove();
    nodeGroup.selectAll("rect").append("title")
        .text(d => `${d.name}\n${(d.value)}`);

    // Returning the rects
    const updatedRects = nodeGroup.selectAll("rect");

    return updatedRects;
};


// ***** LABELS ***** :
// Creating a group that woul hold the text elements.
const createNamesGroup = () => {
    const namesGroup = svg.append("g")
        .attr("font-family", "sans-serif")
        .attr("font-size", 8)
        .attr("font-weight", "bold")

    return namesGroup
};

// Takes the names group and adds text elements by joining the data.
const updateNames = (namesGroup, generatedData) => {
    const updateNames = namesGroup.selectAll("text")
        .data(generatedData.nodes)
        .join("text")
        .attr("x", d => d.x0 < WIDTH / 2 ? d.x1 + 6 : d.x0 - 6)
        .attr("y", d => (d.y1 + d.y0) / 2)
        .attr("dy", "0.35em")
        .attr("text-anchor", d => d.x0 < WIDTH / 2 ? "start" : "end")
        .text(d => d.name.split("_")[0]);

    return updateNames;
};




// ***** LINKS ***** :
// A link is a group that contains a path and a title element

// Creating a group that would holds the links
const createLinksGroup = () => {
    const linksGroup = svg.append("g")
        .attr("fill", "none")
        .attr("stroke-opacity", 0.5);

    return linksGroup;
};

const updateLinksGroup = (linksGroup, generatedData) => {
    // Joining new groups
    const links = linksGroup.selectAll("g")
        .data(generatedData.links)
        .join("g");

    // Removing old paths and adding new ones
    links.selectAll("path").remove();
    links.append("path")
    .transition()
    .duration(700)
        .attr("d", d3.sankeyLinkHorizontal())
        .attr("stroke", d => {
            const option = document.querySelector("#color").value;
            console.log(option);
            if (option === "input") { return colorScale(d.source.name.split("_")[0]) }
            else { return colorScale(d.target.name.split("_")[0]) }
        })
    
        .attr("stroke-width", d => Math.max(1, d.width));

    // Removing old titles and adding new ones
    links.selectAll("title").remove();
    links.append("title")
        .text(d => `${d.source.name} → ${d.target.name}\n${(d.value)}`);

    return linksGroup.selectAll("g");


};





// -------------------- Funtions --------------------

// Defining the width and height of the viewBox
const WIDTH = 900;
const HEIGHT = 400

// Creating an SVG element in the HTML
const svg = d3.select("#sankey-container")
    .append("svg")
    .attr("viewBox", [0, 0, WIDTH, HEIGHT]);


// Creating the Sankey generator
const sankeyGenerator = d3.sankey()
    .nodeId(d => d.name)
    .nodeWidth(15)
    .nodePadding(5)
    .nodeAlign(d3["sankeyLeft"])
    // Defines the layout. [left, top], [right, bottom]
    .extent([[1, 5], [WIDTH - 5, HEIGHT - 5]]);


// Creating a color scale with the range shcemeCategory10
const colorScale = d3.scaleOrdinal(d3.schemeCategory10);

// Creating the Sankey
fetch("data.json").then(response => response.json()).then(json => {
    const generatedData = sankeyGenerator(json);
    const nodeGroup = createNodeGroup();
    const updatedNodes = updateRects(nodeGroup, generatedData);
    const namesGroup = createNamesGroup();
    const updatedNames = updateNames(namesGroup, generatedData);
    const linksGroup = createLinksGroup();
    const updatedLinks = updateLinksGroup(linksGroup, generatedData);


    // Color dropdown
    const colorDropDown = document.querySelector("#color");
    colorDropDown.addEventListener("change", (e) => {
        const option = colorDropDown.value;
        if (option === "input") {
            updatedLinks.selectAll("path").transition().duration(1000).attr("stroke", d => colorScale(d.source.name.split("_")[0]))
        } else {
            updatedLinks.selectAll("path").transition().duration(1000).attr("stroke", d => colorScale(d.target.name.split("_")[0]))

        }


    })

    // Alignment dropdown
    const alignDropDown = document.querySelector("#alignment");
    alignDropDown.addEventListener("change", (e) => {
        const option = alignDropDown.value;
        sankeyGenerator.nodeAlign(d3[`sankey${option}`])
        // re-drawing the Sankey with the new positions
        const newData = sankeyGenerator(json);
        updateRects(nodeGroup, newData);
        updateNames(namesGroup, newData);
        updateLinksGroup(linksGroup, newData);

    })

});

