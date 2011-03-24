package de.hpi.pictureSupport.helper;

import java.util.HashMap;
import java.util.Map;

import de.hpi.pictureSupport.PictureBuildingBlock;
import de.hpi.pictureSupport.PictureProcess;
import de.hpi.pictureSupport.diagram.PictureProcessModel;


public class BlockRepository {

	private Map<Integer, PictureBuildingBlock> blockMap = new HashMap<Integer, PictureBuildingBlock>();
	private Map<Integer, String> blockNameMap = new HashMap<Integer, String>();
	private BlockTypeMapping mapping = new BlockTypeMapping();
	
	public BlockRepository(PictureXML newPicture){
		for (PictureProcess process : newPicture.processes.getChildren()) {
			for (PictureProcessModel model : process.getProcessModels().getChildren()) {
				for (PictureBuildingBlock buildingBlock : model.getBuildingBlockRepository().getChildren()) {
					blockMap.put(buildingBlock.getId(), buildingBlock);
					blockNameMap.put(buildingBlock.getId(), newPicture.getMethodDefinition().getBuildingBlockTypes().getChildrenMap().get(buildingBlock.getType()));
				}
			}
		}
	}
	
	public PictureBuildingBlock findBlock(int blockId){
		return blockMap.get(blockId);
	}
	
	public String findBlockName(int key) {
		return mapping.getInternalStringFor(blockNameMap.get(key));
	}
}
