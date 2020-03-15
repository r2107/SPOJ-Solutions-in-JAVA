import java.io.*;
// Problem Link ->  https://www.spoj.com/problems/IOPC1207/
// Problem Link ->  https://www.codechef.com/problems/IOPC1207
// Solution Link -> https://www.codechef.com/viewsolution/30477124
// Accepted on SPOJ & Codechef both

public class Main {
	// Reader class for FAST INPUT in JAVA
	static class Reader {
		final private int BUFFER_SIZE = 1 << 16;
		private DataInputStream din;
		private byte[] buffer;
		private int bufferPointer, bytesRead;

		public Reader() {
			din = new DataInputStream(System.in);
			buffer = new byte[BUFFER_SIZE];
			bufferPointer = bytesRead = 0;
		}

		public Reader(String file_name) throws IOException {
			din = new DataInputStream(new FileInputStream(file_name));
			buffer = new byte[BUFFER_SIZE];
			bufferPointer = bytesRead = 0;
		}

		public String next() throws IOException {
			byte[] buf = new byte[64]; // line length
			int cnt = 0, c;
			while ((c = read()) != -1) {
				if (c == ' ')
					break;
				buf[cnt++] = (byte) c;
			}
			return new String(buf, 0, cnt);
		}

		public int nextInt() throws IOException {
			int ret = 0;
			byte c = read();
			while (c <= ' ')
				c = read();
			boolean neg = (c == '-');
			if (neg)
				c = read();
			do {
				ret = ret * 10 + c - '0';
			} while ((c = read()) >= '0' && c <= '9');

			if (neg)
				return -ret;
			return ret;
		}

		private void fillBuffer() throws IOException {
			bytesRead = din.read(buffer, bufferPointer = 0, BUFFER_SIZE);
			if (bytesRead == -1)
				buffer[0] = -1;
		}

		private byte read() throws IOException {
			if (bufferPointer == bytesRead)
				fillBuffer();
			return buffer[bufferPointer++];
		}

		public void close() throws IOException {
			if (din == null)
				return;
			din.close();
		}
	}
	
	public static void main(String[] args) throws Exception {
		Reader s = new Reader(); 
		int t = s.nextInt();
		// String Buffer to store results and print only once for FAST OUTPUT in JAVA
		StringBuffer sb = new StringBuffer();
		while(t-- > 0){
			int tx = s.nextInt(),
				ty = s.nextInt(),
				tz = s.nextInt(),
				q = s.nextInt();
			// segment tree will the store the number of red nodes between the corresponding range.
			// lazy tree stores 0 or 1(1 -> to tell the pending update on the node and its children
			int xtree[] = new int[4 * tx]; // segment tree for x-direction
			int ytree[] = new int[4 * ty]; // segment tree for y-direction
			int ztree[] = new int[4 * tz]; // segment tree for z-direction
			int lxtree[] = new int[4 * tx];// lazy tree for x-direction
			int lytree[] = new int[4 * ty];// lazy tree for y-direction
			int lztree[] = new int[4 * tz];// lazy tree for z-direction
			
			for(int i = 0; i < q; i++){
				int type = s.nextInt();
				if(type == 0) {
					int sx = s.nextInt(), ex = s.nextInt();
					update(xtree, lxtree, 0, 1, sx, ex, 0, tx - 1);
				}
				else if(type == 1) {
					int sy = s.nextInt(), ey = s.nextInt();
					update(ytree, lytree, 0, 1, sy, ey, 0, ty - 1);
				}
				else if(type == 2) {
					int sz = s.nextInt(), ez = s.nextInt();
					update(ztree, lztree, 0, 1, sz, ez, 0, tz - 1);
				}
				else {
					int sx = s.nextInt(), sy = s.nextInt(), sz = s.nextInt(),
						ex = s.nextInt(), ey = s.nextInt(), ez = s.nextInt();
					int rx = query(xtree, lxtree, 0, sx, ex, 0, tx - 1), gx = ex - sx + 1 - rx,
						ry = query(ytree, lytree, 0, sy, ey, 0, ty - 1), gy = ey - sy + 1 - ry,
						rz = query(ztree, lztree, 0, sz, ez, 0, tz - 1), gz = ez - sz + 1 - rz;
					long ans = rx * 1l * ry * rz + rx * 1l * gy * gz
							+ gx * 1l * ry * gz + gx * 1l * gy * rz;
					sb.append(ans + "\n");
				}
			}
		}
		System.out.println(sb);
	}

	/*
	 * tree  -> segment tree.
	 * ltree -> lazy tree for lazy propagation.
	 * pos   -> to track the index of current node of trees in the array.
	 * delta -> the value to do update.
	 * start -> start index of our update query.
	 * end   -> end index of our update query.
	 * snode -> starting index of the tree node that stores information of our array.
	 * enode -> ending index of tree node to that sores information of our array.
	 */
	private static void update(int[] tree, int[] ltree, int pos, int delta,
			int start, int end, int snode, int enode) {
		
		if(snode > enode) return;
		
		// do all the lazy propagation if pending at pos
		// and mark is children for propagation
		if(ltree[pos] != 0){
			tree[pos] = enode - snode + 1 - tree[pos];
			// the node is not a leaf node
			if(snode != enode) {
				ltree[2 * pos + 1] ^= delta;
				ltree[2 * pos + 2] ^= delta;
			}
			ltree[pos] = 0;
		}
		
		// no overlap condition
		if(snode > end || enode < start) return;
		
		// total overlap
		if(start <= snode && end >= enode) {
			tree[pos] = enode - snode + 1 - tree[pos];
			// the node is not the leaf node
			if(snode != enode) {
				ltree[2 * pos + 1] ^= delta;
				ltree[2 * pos + 2] ^= delta;
			}
			return;
		}
		
		// partial overlap
		int mid = (snode + enode) / 2;
		update(tree, ltree, 2 * pos + 1, delta, start, end, snode, mid);
		update(tree, ltree, 2 * pos + 2, delta, start, end, mid + 1, enode);
		tree[pos] = tree[2 * pos + 1] + tree[2 * pos + 2];
	}

	private static int query(int[] tree, int[] ltree, int pos, int start, int end, int snode, int enode) {
		if(snode > enode) return 0;
		
		// do all the lazy propagation if pending at pos
		// and mark is children for propagation
		if(ltree[pos] != 0){
			tree[pos] = enode - snode + 1 - tree[pos];
			// the node is not a leaf node
			if(snode != enode) {
				ltree[2 * pos + 1] ^= 1;
				ltree[2 * pos + 2] ^= 1;
			}
			ltree[pos] = 0;
		}
		
		// no overlap condition
		if(snode > end || enode < start) return 0;
		
		// total overlap
		if(start <= snode && end >= enode) {
			return tree[pos];
		}
		
		// partial overlap
		int mid = (snode + enode) / 2;
		int op1 = query(tree, ltree, 2 * pos + 1, start, end, snode, mid);
		int op2 = query(tree, ltree, 2 * pos + 2, start, end, mid + 1, enode);
		return op1 + op2;
	}
}