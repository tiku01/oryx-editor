package de.hpi.compatibility;

import de.hpi.compatibility.NodeUtil.PartitioningMode;

public class PathInfo {

	public enum Part {
		C1, C2, Interleaving
	}

	private Part finalPart = null;
	private Part startPart = null;

	public PathInfo(PartitioningMode startMode) {
		switch (startMode) {
		case Interleaving:
			startPart = Part.Interleaving;
			break;
		case C1:
			startPart = Part.C1;
			break;
		case C2:
			startPart = Part.C2;
			break;
		}
	}

	public PathInfo(PartitioningMode startMode, PartitioningMode finalMode) {
		switch (startMode) {
		case Interleaving:
			startPart = Part.Interleaving;
			break;
		case C1:
			startPart = Part.C1;
			break;
		case C2:
			startPart = Part.C2;
			break;
		}

		switch (finalMode) {
		case Interleaving:
			finalPart = Part.Interleaving;
			break;
		case C1:
			finalPart = Part.C1;
			break;
		case C2:
			finalPart = Part.C2;
			break;
		}
	}

	public Part getFinalPart() {
		return finalPart;
	}

	public Part getStartPart() {
		return startPart;
	}

	public boolean hasOrderedStart() {
		if (startPart == Part.C1 || startPart == Part.C2)
			return true;
		return false;
	}

	public boolean hasOrderedEnd() {
		if (finalPart == Part.C1 || finalPart == Part.C2)
			return true;
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != PathInfo.class) {
			return false;
		} else {
			PathInfo info = (PathInfo) obj;
			if (this.startPart == info.getStartPart() && this.finalPart == info.getFinalPart())
				return true;
		}

		return false;
	}

}