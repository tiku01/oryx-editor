package de.hpi.pictureSupport.helper;

import java.util.HashMap;
import java.util.Map;

import de.hpi.pictureSupport.PictureBuildingBlock;
import de.hpi.pictureSupport.PictureProcess;
import de.hpi.pictureSupport.diagram.PictureProcessModel;


/**
 * The BlockRepository, holding the blocks and the PICTURE names of the blocks specified in the XML repository parts.
 */
public class BlockRepository {

	private static Map<Integer, PictureBuildingBlock> blockMap = new HashMap<Integer, PictureBuildingBlock>();
	private static Map<Integer, String> blockNameMap = new HashMap<Integer, String>();
	
	/**
	 * Initialize the repository and build the mappings.
	 * blockMap:
	 * 	<key>	block ID given in the XML's variants' buildingBlockOccurences
	 * 	<value>	related building block, specified in the XML's process model's building block repository
	 * 
	 * blockNameMap:
	 * 	<key>	block ID given in the XML variants' buildingBlockOccurences
	 * 	<value>	name of the related building block in the XML's method dictionary
	 *
	 * @param newPicture the new picture
	 */
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
	
	/**
	 * Find block related to the given ID.
	 *
	 * @param blockId the block id
	 * @return the picture building block
	 */
	public static PictureBuildingBlock findBlock(int blockId){
		return blockMap.get(blockId);
	}
	
	/**
	 * Find block name related to the given ID.
	 *
	 * @param key the key
	 * @return the string
	 */
	public static String findBlockName(int key) {
		return BlockTypeMapping.getInternalStringFor(blockNameMap.get(key));
	}
}
