package de.hpi.pictureSupport.helper;

import java.util.HashMap;
import java.util.Map;

import de.hpi.pictureSupport.PictureBuildingBlock;
import de.hpi.pictureSupport.PictureProcess;
import de.hpi.pictureSupport.diagram.PictureProcessModel;


public class BlockRepository {

	private static Map<Integer, PictureBuildingBlock> blockMap = new HashMap<Integer, PictureBuildingBlock>();
	private static Map<Integer, String> blockNameMap = new HashMap<Integer, String>();
	
	public static void initializeRepository(PictureXML newPicture){
		for (PictureProcess process : newPicture.getProcesses().getChildren()) {
			for (PictureProcessModel model : process.getProcessModels().getChildren()) {
				for (PictureBuildingBlock buildingBlock : model.getBuildingBlockRepository().getChildren()) {
					blockMap.put(buildingBlock.getId(), buildingBlock);
					blockNameMap.put(buildingBlock.getId(), newPicture.getMethodDefinition().getBuildingBlockTypes().getChildrenMap().get(buildingBlock.getType()));
				}
			}
		}
	}
	
	public static PictureBuildingBlock findBlock(int blockId){
		return blockMap.get(blockId);
	}
	
	public static String findBlockName(int key) {
		return BlockTypeMapping.getInternalStringFor(blockNameMap.get(key));
	}
}
