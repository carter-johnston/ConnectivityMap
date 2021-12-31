import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class AssignmentX2 {
	public static void main(String[] args) throws IOException {
		Map map = null;
		ArrayList<String> set = fileRead("graph.txt");
		boolean flag = true;
		for(String temp:set) {
			String[] tokens = temp.split(" ");
			ArrayList<String> list = new ArrayList<>();
			for(String token:tokens) {
				list.add(token);
			}
			if (flag == true){
				map = new Map(list,"Map");
				flag = false;
			}
			else {
				map.insert(list);
			}
		}
		System.setProperty("org.graphstream.ui","swing");
		try {
			map.graph.display();
		}
		catch(NullPointerException e) {
			System.out.println("graph is empty.");
			System.out.println("-program terminated-");
			return;
		}
		map.BFS();
	}
 	public static ArrayList<String> fileRead(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String data;
		ArrayList<String> set = new ArrayList<String>();
		while((data = br.readLine()) != null) {
			set.add(data);
		}
		br.close();
		return set;
	}
	static class Map{
		public Graph graph;
		public ArrayList<Vertex> vertices;
		public ArrayList<Edge> edges;
		String styleSheet = 
		        "node {"+
	                "fill-color: black;"+
	        		"size: 20px;"+
	                "text-size: 30;"+"text-alignment: at-right;"+"text-color: black;"+
	        		"text-visibility-mode: normal;"+
                "}"+
                "node.blue {"+
                	"fill-color: blue;"+
                "}"+
                "node.yellow {"+
            	"fill-color: yellow;"+
            	"}"+
                "node.green {"+
            	"fill-color: green;"+
                "}"+
                "node.purple {"+
            	"fill-color: purple;"+
                "}"+
		        "node.red {"+
		        	"fill-color: red;"+
		        "}";

		public Map(ArrayList<String> line,String name){
			graph = new SingleGraph("Graph "+name);
			vertices = new ArrayList<>();
			edges = new ArrayList<>();

			Node base = graph.addNode(line.get(0));
			base.setAttribute("ui.label", base.getId());
			
			Vertex v = new Vertex(line.remove(0),base);
			
			for(String temp:line) {
				Node node = graph.addNode(temp);
				node.setAttribute("ui.label", temp);
				
				Vertex u = new Vertex(temp,node);
				u.addEdge(v);
				v.addEdge(u);
				vertices.add(u);
			}
			vertices.add(v);
			
			for(String temp:line) {//visual edges
				Edge e = graph.addEdge(base.getId()+temp, base.getId(), temp);
				edges.add(e);
			}
			graph.setAttribute("ui.stylesheet",styleSheet);
		}
		boolean exist(Vertex vertex, ArrayList<Vertex> list) {
			for(Vertex v:list) {
				if(v.name.equals(vertex.name)) {
					return true;
				}
			}
			return false;
		}
		int locateVertex(Vertex vertex,ArrayList<Vertex> list) {
			for(int i = 0;i<list.size();i++) {
				if(list.get(i).name.equals(vertex.name)) {
					return i;
				}
			}
			return -1;
		}
		
		void BFS() {
			int size = vertices.size();
			ArrayList<Vertex> unvisited = vertices;
			ArrayList<Vertex> visited = new ArrayList<>();
			Queue<Vertex> queue = new LinkedList<>();
			int cluster = 1;
			boolean single = false;
			String[] color = {"blue","red","yellow","purple","green"};
			
			while(!unvisited.isEmpty()) {
				Vertex current = unvisited.remove(0);
				queue.add(current);
				while(!queue.isEmpty()) {
					do{
						for(Vertex neighbor:current.neighbors) {
							int address = locateVertex(neighbor,unvisited);
							if(address != -1) {
								queue.add(neighbor);
								unvisited.remove(address);
							}
						}
						if(!queue.isEmpty()) {
							visited.add(queue.remove());
							current = queue.peek();
						}
					}while(current != null && !current.neighbors.isEmpty());
				}
				if(visited.size() == size) {
					single = true;
				}
				 for(Vertex v:visited) {
					 try {
						 v.node.setAttribute("ui.class", color[cluster-1]);
					 }catch(ArrayIndexOutOfBoundsException e) {
						 System.out.println("oops, we've ran out of color options. Maximum amount of five clusters only.");
						 break;
					 }
				 }
				 System.out.print("Cluster "+cluster+": {");
				 cluster++;
				 while(!visited.isEmpty()) {
					 Vertex v = visited.remove(0);
					 System.out.print(v.name);
					 if(!visited.isEmpty()) {
						 System.out.print(", ");
					 }
				 }
				 System.out.println("}");
			 }
			 if(single == true) {
				 System.out.println();
				 System.out.println("The input graph is connected");
			 }
			 else {
				 System.out.println();
				 System.out.println("The input graph is not connected");
			 }
		}
		
		public void insert(ArrayList<String> line){
			Vertex base = null;
			String element = line.remove(0);
			int baseAddy = locateVertex(element);
			
			if(baseAddy == -1) {
				Node n = graph.addNode(element);
				n.setAttribute("ui.label",element);
				base = new Vertex(element,n);
			}
			else {
				base = vertices.get(baseAddy);
			}
			for(String temp:line) {
				int vertAddy = locateVertex(temp);
				if(vertAddy == -1) {
					Node node = graph.addNode(temp);
					node.setAttribute("ui.label",temp);
					
					Vertex v = new Vertex(temp,node);
					base.addEdge(v);
					v.addEdge(base);
					vertices.add(v);
					Edge e = graph.addEdge(base.node.getId()+v.node.getId(), base.node.getId(),v.node.getId());
					edges.add(e);
				}
				else {
					Vertex v = vertices.get(vertAddy);
					base.addEdge(v);
					v.addEdge(base);
					vertices.set(vertAddy, v);
					Edge e = graph.addEdge(base.node.getId()+v.node.getId(), base.node.getId(),v.node.getId());
					edges.add(e);
				}
			}
			if(baseAddy == -1) {
				vertices.add(base);
			}
			else {
				vertices.set(baseAddy, base);
			}
		}
		
		public int locateVertex(String element) {
			int address = -1;
			for(int i = 0;i<vertices.size();++i) {
				if(vertices.get(i).name.equals(element)) {
					address = i;
					break;
				}
			}
			return address;
		}
	
		class Vertex{
			String name;
			Node node;
			ArrayList<Vertex> neighbors;
			
			
			public Vertex(String element,Node node) {
				this.name = element;
				this.node = node;
				neighbors = new ArrayList<>();
			}
			public void addEdge(Vertex neighbor) {
				neighbors.add(neighbor);
			}
		}//Class Vertex
	}//Class Map
}
